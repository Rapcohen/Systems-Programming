package bgu.spl.net.srv;

import bgu.spl.net.api.Message;
import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.messages.*;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class BGRSEncDec implements MessageEncoderDecoder<Message> {

    private ByteBuffer opBuffer = ByteBuffer.allocate(2);
    private byte[] bytes = null;
    private int len = 0;
    private short opcode = -1;
    private int stringCount = 0;

    @Override
    public Message decodeNextByte(byte nextByte) {
        if (bytes == null) {
            opBuffer.put(nextByte);
            if (!opBuffer.hasRemaining()) {
                opBuffer.flip();
                bytes = new byte[1 << 10]; //start with 1k
                len = 0;
                opcode = bytesToShort(opBuffer.array());
                opBuffer.clear();
            }
            switch (opcode) {
                case 4:
                case 11:
                    return popMsg();
                default:
                    return null;
            }
        } else { // if the program gets here, then 'nextByte' is a byte that is *not opcode byte*
            switch (opcode) {
                case 1:
                case 2:
                case 3:
                    //expecting: [string, 0, string, 0]
                    if (nextByte == '\0') {
                        if (stringCount == 1) {
                            return popMsg();
                        } else {
                            stringCount++;
                        }
                    }
                    pushByte(nextByte);
                    break;
                case 5:
                case 6:
                case 7:
                case 9:
                case 10:
                    //expecting: [byte, byte] (short)
                    if (len == 1) {
                        pushByte(nextByte);
                        return popMsg();
                    }
                    pushByte(nextByte);
                    break;
                case 8:
                    //expecting: [string, 0]
                    if (nextByte == '\0') {
                        return popMsg();
                    }
                    pushByte(nextByte);
                    break;
            }
        }
        return null;
    }

    @Override
    public byte[] encode(Message message) {
        String[] msgInfo = message.toString().split("\0");
        byte[] opcodeBytes = shortToBytes(Short.parseShort(msgInfo[0]));
        byte[] msgOpcodeBytes = shortToBytes(Short.parseShort(msgInfo[1]));
        byte[] encodedMsg;
        byte[] attachmentBytes;

        //Handle ACK Message
        if (message.getOpcode() == 12) {
            if (msgInfo.length == 3) {
                //Message has optional attachment
                attachmentBytes = msgInfo[2].getBytes();
            } else {
                //Message has no optional attachment
                attachmentBytes = null;
            }
            int attachmentLength = (attachmentBytes != null) ? attachmentBytes.length : 0;
            encodedMsg = new byte[5 + attachmentLength];
            fillAnswerArray(encodedMsg, opcodeBytes, msgOpcodeBytes, attachmentBytes, true);
            return encodedMsg;
        }
        //Handle ERR Message
        else { //message.getOpcode() == 13
            encodedMsg = new byte[4];
            fillAnswerArray(encodedMsg, opcodeBytes, msgOpcodeBytes, null, false);
            return encodedMsg;
        }
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }

        bytes[len++] = nextByte;
    }

    private Message popMsg() {
        Message msg = null;
        switch (opcode) {
            case 1:
                String[] adminInfo = parseString();
                msg = new ADMINREG(adminInfo[0], adminInfo[1]);
                break;
            case 2:
                String[] studentInfo = parseString();
                msg = new STUDENTREG(studentInfo[0], studentInfo[1]);
                break;
            case 3:
                String[] loginInfo = parseString();
                msg = new LOGIN(loginInfo[0], loginInfo[1]);
                break;
            case 4:
                msg = new LOGOUT();
                break;
            case 5:
                msg = new COURSEREG(bytesToShort(bytes));
                break;
            case 6:
                msg = new KDAMCHECK(bytesToShort(bytes));
                break;
            case 7:
                msg = new COURSESTAT(bytesToShort(bytes));
                break;
            case 8:
                msg = new STUDENTSTAT(new String(bytes, 0, len, StandardCharsets.UTF_8));
                break;
            case 9:
                msg = new ISREGISTERED(bytesToShort(bytes));
                break;
            case 10:
                msg = new UNREGISTER(bytesToShort(bytes));
                break;
            case 11:
                msg = new MYCOURSES();
                break;
        }
        bytes = null;
        opcode = -1;
        stringCount = 0;
        return msg;
    }

    private String[] parseString() {
        String string = new String(bytes, 0, len, StandardCharsets.UTF_8);
        return string.split("\0");
    }

    private short bytesToShort(byte[] byteArr) {
        short result = (short) ((byteArr[0] & 0xff) << 8);
        result += (short) (byteArr[1] & 0xff);
        return result;
    }

    private byte[] shortToBytes(short num) {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte) ((num >> 8) & 0xFF);
        bytesArr[1] = (byte) (num & 0xFF);
        return bytesArr;
    }

    private void fillAnswerArray(byte[] answer, byte[] opcode, byte[] msgOpcode, byte[] attachment, boolean isACK) {
        for (int i = 0; i < 2; i++) {
            answer[i] = opcode[i];
            answer[i + 2] = msgOpcode[i];
        }
        if (attachment != null) {
            System.arraycopy(attachment, 0, answer, 4, attachment.length);
        }
        if (isACK) {
            answer[answer.length - 1] = "\0".getBytes()[0];
        }
    }
}
