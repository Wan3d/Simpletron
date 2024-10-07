import java.util.Scanner;

/**
 * @author Kurt P
 * @version 1.0.0.01082013
 */
public class Simpletron extends SimpletronOperationCodes {

    private int[] memory = new int[1000];
    private int accumulator;
    private int instructionCounter;
    private int instructionRegister;
    private int operationCode;
    private int operand;
    private boolean run = true;

    /**
     * The <code>run</code> method will start Simpletron, display the welcome
     * message and then go straight into SML execution.
     *
     * @return void
     */
    public void run() {
        welcomeMessage();
        execute();
    }

    /**
     * The welcome message when Simpletron is first started.
     *
     * @return void
     */
    private void welcomeMessage() {
        System.out.println("***            Welcome to Simpletron!           ***");
        System.out.println("*** Please enter your program, one instruction  ***");
        System.out.println("*** (or data word) at a time in hexadecimal.    ***");
        System.out.println("*** I will display the location number and a    ***");
        System.out.println("*** question mark (?). You then type the word   ***");
        System.out.println("*** for that location. Type -99999 to stop.     ***");
    }

    private void execute() {
        Scanner codeInputter = new Scanner(System.in);
        String instructionInput;
        int memoryPointer = 0;

        do {
            // Output the code input prompt
            System.out.printf("%02d ? ", memoryPointer);
            // Take the user input and assign it to the input var, in hex
            instructionInput = codeInputter.next();

            // Check for the termination input (-99999 in hex is FFFF9FFF)
            if (!instructionInput.equalsIgnoreCase("FFFF9FFF")) {
                // Convert the input to an integer (hex input)
                memory[memoryPointer] = Integer.parseInt(instructionInput, 16);
                // Increment the pointer by one
                memoryPointer++;
            }

        } while (!instructionInput.equalsIgnoreCase("FFFF9FFF"));

        System.out.printf("\n%s\n%s\n\n", "***  Program loading complete ***",
                "*** Program execution begins ***");

        while (run) {
            loadCode();
            operations(operationCode, operand);
        }
        codeInputter.close();
        System.exit(0);
    }

    /**
     * Using the <code>instructionRegister</code> var, <code>loadCode()</code>
     * will determine what operation will be executed and which memory location
     * it will need to access to complete that operation.
     *
     * @return void
     */
    private void loadCode() {
        instructionRegister = memory[instructionCounter];
        operationCode = instructionRegister / 100;
        operand = instructionRegister % 100;
    }

    /**
     * Once the operation and operand are determined by <code>loadCode()</code>
     * they are executed.
     *
     * @param operationCode
     * @param operand
     * @return void
     */
    private void operations(int operationCode, int operand) {
        boolean branching = false;

        switch (operationCode) { //Start switch

            // Operations for reading input from the user
            case READ:
                Scanner read = new Scanner(System.in);
                System.out.print("Enter a number (hexadecimal): ");
                String hexNumber = read.next();
                memory[operand] = Integer.parseInt(hexNumber, 16);
                break;

            // Operations for outputting to the user
            case WRITE:
                System.out.println(Integer.toHexString(memory[operand]).toUpperCase());
                break;

            // Load the value found in memory into the accumulator
            case LOAD:
                accumulator = memory[operand];
                break;

            // Put the value in the accumulator into memory
            case STORE:
                memory[operand] = accumulator;
                break;

            // Add the value in the accumulator and a value from memory
            case ADD:
                accumulator += memory[operand];
                break;

            // Subtract the value in the accumulator and a value in memory
            case SUBTRACT:
                accumulator -= memory[operand];
                break;

            // Divide the value in the accumulator by a value in memory
            case DIVIDE:
                // Can't divide by zero.
                if (memory[operand] == 0) {
                    System.out.printf("\n%s\n%s\n", "*** CANNOT DIVIDE BY ZERO ***", "*** EXITING NOW ***");
                    System.exit(-1);
                } else {
                    accumulator /= memory[operand];
                }
                break;

            // Multiply the value in the accumulator by a value in memory
            case MULITPLY:
                accumulator *= memory[operand];
                break;

            case REMAINDER:
                accumulator %= memory[operand];
                break;

            case EXPONENT:
                accumulator = (int) Math.pow(accumulator, memory[operand]);
                break;

            // Branch to a specific memory location
            case BRANCH:
                instructionCounter = operand;
                branching = true;
                break;

            // Branch to a memory location if the accumulator is less than zero
            case BRANCHNEG:
                if (accumulator < 0) {
                    instructionCounter = operand;
                    branching = true;
                }
                break;

            // Branch to a memory location if the accumulator is zero
            case BRANCHZERO:
                if (accumulator == 0) {
                    instructionCounter = operand;
                    branching = true;
                }
                break;

            // Finish processing
            case HALT:
                System.out.println("Processing complete...");
                run = false;
                memoryDump();
                break;

        } // End switch

        if (!branching) {
            instructionCounter++;
        }
    }

    /**
     * Outputs the values found in the <code>memory</code>
     *
     * @return void
     */
    private void memoryDump() {
        System.out.printf("\t%02X\t%02X\t%02X\t%02X\t%02X\t%02X\t%02X\t%02X\t%02X\t%02X\n", 0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        for (int tens = 0; tens < 1000; tens += 10) {
            System.out.printf("%02X\t", tens);
            for (int ones = 0; ones < 10; ones++) {
                System.out.printf("%04X\t", memory[tens + ones]);
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        Simpletron simpletron = new Simpletron();
        simpletron.run();
    }
}
