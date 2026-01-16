/**
 * KnockKnockProtocol - Implements a finite state machine for the knock-knock joke protocol.
 * 
 * This educational example demonstrates:
 * - State machine pattern: Each state represents a step in the knock-knock joke
 * - Protocol implementation: Defines rules for valid client-server communication
 * - State transitions: How to move between different conversation states
 * - Input validation: Checking if client input is appropriate for the current state
 * 
 * State Diagram:
 *   START -> WAITING -> SENTKNOCKKNOCK -> SENTCLUE -> ANOTHER -> SENTKNOCKKNOCK (loop)
 *                                            |
 *                                            v
 *                                         (exit)
 * 
 * The joke data is stored in parallel arrays: clues and answers.
 * Index i in clues corresponds to index i in answers.
 */
public class KnockKnockProtocol {
    // State constants - define the protocol conversation states
    private static final int WAITING = 0;           // Ready to start a new joke
    private static final int SENTKNOCKKNOCK = 1;    // Sent "Knock! Knock!" - waiting for "Who's there?"
    private static final int SENTCLUE = 2;          // Sent the clue - waiting for "[Clue] who?"
    private static final int ANOTHER = 3;           // Sent the answer - asking if user wants another

    private static final int NUM_JOKES = 5;         // Total number of jokes in the database

    // Instance variables
    private int state = WAITING;                     // Current conversation state
    private int currentJoke = 0;                     // Index of the current joke

    // Parallel arrays: Each joke is a clue-answer pair
    private String[] clues = {
        "Turnip",
        "Little Old Lady",
        "Atch",
        "Who",
        "Who"
    };

    private String[] answers = {
        "Turnip the heat, it's cold in here!",
        "I didn't know you could yodel!",
        "Bless you!",
        "Is there an owl in here?",
        "Is there an echo in here?"
    };

    /**
     * Process client input and return an appropriate server response.
     * This method implements the state machine logic.
     * 
     * @param clientInput The message received from the client. Can be null for the initial call.
     * @return The server's response message
     */
    public String processInput(String clientInput) {
        String response = null;

        // STATE 0: WAITING - Server is ready to start
        if (state == WAITING) {
            response = "Knock! Knock!";
            state = SENTKNOCKKNOCK;
            System.out.println("[Protocol Debug] State transition: WAITING -> SENTKNOCKKNOCK");
        }
        
        // STATE 1: SENTKNOCKKNOCK - Waiting for client to ask "Who's there?"
        else if (state == SENTKNOCKKNOCK) {
            if (clientInput.equalsIgnoreCase("Who's there?")) {
                // Client gave correct response - reveal the clue
                response = clues[currentJoke];
                state = SENTCLUE;
                System.out.println("[Protocol Debug] State transition: SENTKNOCKKNOCK -> SENTCLUE");
            } else {
                // Client gave wrong response - prompt them again
                response = "You're supposed to say \"Who's there?\"! Try again. Knock! Knock!";
                // Stay in SENTKNOCKKNOCK state
                System.out.println("[Protocol Debug] Invalid input in SENTKNOCKKNOCK state. Input: '" + clientInput + "'");
            }
        }
        
        // STATE 2: SENTCLUE - Waiting for client to ask "[Clue] who?"
        else if (state == SENTCLUE) {
            String expectedInput = clues[currentJoke] + " who?";
            if (clientInput.equalsIgnoreCase(expectedInput)) {
                // Client gave correct response - reveal the answer
                response = answers[currentJoke] + " Want another? (y/n)";
                state = ANOTHER;
                System.out.println("[Protocol Debug] State transition: SENTCLUE -> ANOTHER");
            } else {
                // Client gave wrong response - remind them what to say
                response = "You're supposed to say \"" + expectedInput + "\"! Try again. Knock! Knock!";
                state = SENTKNOCKKNOCK;
                System.out.println("[Protocol Debug] Invalid input in SENTCLUE state. Expected: '" + expectedInput + "', got: '" + clientInput + "'");
            }
        }
        
        // STATE 3: ANOTHER - Waiting for user to request another joke (y/n)
        else if (state == ANOTHER) {
            if (clientInput.equalsIgnoreCase("y")) {
                // Client wants another joke - start the next one
                response = "Knock! Knock!";
                
                // Advance to the next joke (wrap around to the first if at the end)
                if (currentJoke == (NUM_JOKES - 1)) {
                    currentJoke = 0;
                } else {
                    currentJoke++;
                }
                
                state = SENTKNOCKKNOCK;
                System.out.println("[Protocol Debug] State transition: ANOTHER -> SENTKNOCKKNOCK (Joke #" + (currentJoke + 1) + ")");
            } else {
                // Client wants to quit
                response = "Bye.";
                state = WAITING;
                System.out.println("[Protocol Debug] State transition: ANOTHER -> WAITING");
            }
        }

        return response;
    }
}
