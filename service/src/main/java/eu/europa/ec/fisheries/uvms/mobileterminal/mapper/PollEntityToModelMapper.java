/*
﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
© European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.europa.ec.fisheries.uvms.mobileterminal.mapper;

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.*;
import eu.europa.ec.fisheries.uvms.mobileterminal.constants.MobileTerminalConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.Poll;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.PollBase;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.PollProgram;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PollEntityToModelMapper {

    private final static String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss Z";
    private final static String DATE_TIME_FORMAT_WO_TIMEZONE = "yyyy-MM-dd HH:mm:ss";

    private static PollResponseType mapToPollResponseType(PollBase pollBase, MobileTerminal mobileTerminal)  {
        PollResponseType response = new PollResponseType();
        response.setComment(pollBase.getComment());
        response.setUserName(pollBase.getUpdatedBy());
        // TODO created time?
        response.setMobileTerminal(MobileTerminalEntityToModelMapper.mapToMobileTerminalType(mobileTerminal));
        response.getAttributes().add(createPollAttribute(PollAttributeType.USER, pollBase.getUpdatedBy()));
        return response;
    }

    public static PollResponseType mapToPollResponseType(PollProgram program, MobileTerminal mobileTerminal) {
        PollResponseType response = mapToPollResponseType(program.getPollBase(), mobileTerminal);
        response.setPollType(PollType.PROGRAM_POLL);
        PollId pollId = new PollId();
        pollId.setGuid(program.getId().toString());
        response.setPollId(pollId);

        response.getAttributes().addAll(getProgramPollAttributes(program));
        return response;
    }

    public static PollResponseType mapToPollResponseType(Poll poll, MobileTerminal mobileTerminal, PollType pollType)  {
        PollResponseType response = mapToPollResponseType(poll.getPollBase(), mobileTerminal);
        response.setPollType(pollType);
        PollId pollId = new PollId();
        pollId.setGuid(poll.getId().toString());
        response.setPollId(pollId);
        
        return response;
    }

    private static List<PollAttribute> getProgramPollAttributes(PollProgram program) {
        List<PollAttribute> attributes = new ArrayList<>();
        attributes.add(createPollAttribute(PollAttributeType.FREQUENCY, program.getFrequency().toString()));
        attributes.add(createPollAttribute(PollAttributeType.START_DATE, program.getStartDate().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT))));
        attributes.add(createPollAttribute(PollAttributeType.END_DATE, program.getStopDate().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT))));

        switch (program.getPollState()) {
        case STARTED:
        	attributes.add(createPollAttribute(PollAttributeType.PROGRAM_RUNNING, MobileTerminalConstants.TRUE));
            break;
        case STOPPED:
        case ARCHIVED:
        	attributes.add(createPollAttribute(PollAttributeType.PROGRAM_RUNNING, MobileTerminalConstants.FALSE));
            break;
        }
        return attributes;
    }

    private static PollAttribute createPollAttribute(PollAttributeType key, String value) {
        PollAttribute attr = new PollAttribute();
        attr.setKey(key);
        attr.setValue(value);
        return attr;
    }
}