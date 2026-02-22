package sagiri.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import sagiri.exception.SagiriException;

/**
 * Test class for Parser, covering various command parsing scenarios and edge cases.
 */
public class ParserTest {

    @Test
    public void testParseList() throws SagiriException {
        Parser.ParsedCommand cmd = Parser.parse("list");
        assertEquals(Parser.CommandType.LIST, cmd.getType());
    }

    @Test
    public void testParseMark() throws SagiriException {
        Parser.ParsedCommand cmd = Parser.parse("mark 3");
        assertEquals(Parser.CommandType.MARK, cmd.getType());
        assertEquals(2, cmd.getTaskIndex());
    }

    @Test
    public void testParseUnmark() throws SagiriException {
        Parser.ParsedCommand cmd = Parser.parse("unmark 1");
        assertEquals(Parser.CommandType.UNMARK, cmd.getType());
        assertEquals(0, cmd.getTaskIndex());
    }

    @Test
    public void testParseDelete() throws SagiriException {
        Parser.ParsedCommand cmd = Parser.parse("delete 5");
        assertEquals(Parser.CommandType.DELETE, cmd.getType());
        assertEquals(4, cmd.getTaskIndex());
    }

    @Test
    public void testParseTodo() throws SagiriException {
        Parser.ParsedCommand cmd = Parser.parse("todo buy groceries");
        assertEquals(Parser.CommandType.TODO, cmd.getType());
        assertEquals("buy groceries", cmd.getData());
    }

    @Test
    public void testParseEvent() throws SagiriException {
        Parser.ParsedCommand cmd = Parser.parse("event meeting /from 01-01-25 /to 01-01-25");
        assertEquals(Parser.CommandType.EVENT, cmd.getType());
        assertEquals("meeting /from 01-01-25 /to 01-01-25", cmd.getData());
    }

    @Test
    public void testParseDeadline() throws SagiriException {
        Parser.ParsedCommand cmd = Parser.parse("deadline submit report /by 15-02-25");
        assertEquals(Parser.CommandType.DEADLINE, cmd.getType());
        assertEquals("submit report /by 15-02-25", cmd.getData());
    }

    @Test
    public void testParseCheck() throws SagiriException {
        Parser.ParsedCommand cmd = Parser.parse("check 25-12-24");
        assertEquals(Parser.CommandType.CHECK, cmd.getType());
        assertEquals("25-12-24", cmd.getData());
    }

    @Test
    public void testParseFind() throws SagiriException {
        Parser.ParsedCommand cmd = Parser.parse("find book");
        assertEquals(Parser.CommandType.FIND, cmd.getType());
        assertEquals("book", cmd.getData());
    }

    @Test
    public void testParseBye() throws SagiriException {
        Parser.ParsedCommand cmd = Parser.parse("bye");
        assertEquals(Parser.CommandType.BYE, cmd.getType());
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

    @Test
    public void testParseFindEmptyKeyword() {
        assertThrows(SagiriException.class, () -> Parser.parse("find"));
    }
}
