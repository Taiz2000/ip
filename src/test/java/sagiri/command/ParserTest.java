package sagiri.command;

import sagiri.exception.SagiriException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ParserTest {

    @Test
    public void testParseList() throws SagiriException {
        Parser.ParsedCommand cmd = Parser.parse("list");
        assertEquals(Parser.CommandType.LIST, cmd.type);
    }

    @Test
    public void testParseMark() throws SagiriException {
        Parser.ParsedCommand cmd = Parser.parse("mark 3");
        assertEquals(Parser.CommandType.MARK, cmd.type);
        assertEquals(2, cmd.taskIndex);
    }

    @Test
    public void testParseUnmark() throws SagiriException {
        Parser.ParsedCommand cmd = Parser.parse("unmark 1");
        assertEquals(Parser.CommandType.UNMARK, cmd.type);
        assertEquals(0, cmd.taskIndex);
    }

    @Test
    public void testParseDelete() throws SagiriException {
        Parser.ParsedCommand cmd = Parser.parse("delete 5");
        assertEquals(Parser.CommandType.DELETE, cmd.type);
        assertEquals(4, cmd.taskIndex);
    }

    @Test
    public void testParseTodo() throws SagiriException {
        Parser.ParsedCommand cmd = Parser.parse("todo buy groceries");
        assertEquals(Parser.CommandType.TODO, cmd.type);
        assertEquals("buy groceries", cmd.data);
    }

    @Test
    public void testParseEvent() throws SagiriException {
        Parser.ParsedCommand cmd = Parser.parse("event meeting /from 01-01-25 /to 01-01-25");
        assertEquals(Parser.CommandType.EVENT, cmd.type);
        assertEquals("meeting /from 01-01-25 /to 01-01-25", cmd.data);
    }

    @Test
    public void testParseDeadline() throws SagiriException {
        Parser.ParsedCommand cmd = Parser.parse("deadline submit report /by 15-02-25");
        assertEquals(Parser.CommandType.DEADLINE, cmd.type);
        assertEquals("submit report /by 15-02-25", cmd.data);
    }

    @Test
    public void testParseCheck() throws SagiriException {
        Parser.ParsedCommand cmd = Parser.parse("check 25-12-24");
        assertEquals(Parser.CommandType.CHECK, cmd.type);
        assertEquals("25-12-24", cmd.data);
    }

    @Test
    public void testParseBye() throws SagiriException {
        Parser.ParsedCommand cmd = Parser.parse("bye");
        assertEquals(Parser.CommandType.BYE, cmd.type);
    }

    @Test
    public void testParseInvalidCommand() {
        assertThrows(SagiriException.class, () -> Parser.parse("invalid"));
    }

    @Test
    public void testParseMarkInvalidNumber() {
        assertThrows(SagiriException.class, () -> Parser.parse("mark abc"));
    }

    @Test
    public void testParseCheckInvalidDate() {
        assertThrows(SagiriException.class, () -> Parser.parse("check 99-99-99"));
    }
}
