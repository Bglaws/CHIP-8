package chip;

public class Chip {

	private short opcode;
	private char[] memory = new char[4096];
	private char[] V = new char[16];

	// Index register & Program counter, which can have a value from 0x000 to 0xFFF
	private char I;
	private short pc;

	// Not sure what memory map is but it goes here

	private char[] gfx = new char[64 * 32];

	// timers
	private char delay_timer;
	private char sound_timer;
	
	// stack and stack pointer
	private char stack[];
	private char stackPointer;

	// hex based keypad
	private char[] k = new char[16];

	public void init{
		
	}
}
