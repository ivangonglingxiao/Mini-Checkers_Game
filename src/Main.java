import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Hashtable;

public class Main extends javax.swing.JApplet {
	private static final long serialVersionUID = 1L;
    private Game game;
    private BoardLayeredPane boardPanel;
    private JLabel redPlayerNoPieces, bluePlayerNoPieces, currentPlayerLabel;
    private JSlider difficultySlider;
    private JRadioButtonMenuItem easyLevel, mediumLevel, hardLevel;

    private ImageIcon redChecker = new javax.swing.ImageIcon(getClass().getResource(("/images/RedChecker.gif")));
    private ImageIcon blueChecker = new javax.swing.ImageIcon(getClass().getResource(("/images/BlueChecker.gif")));

    private int currentPlayer = 0;
    private int winner = -1;
    private boolean displayedWinner = false;
    
    public void setSize(int width, int height){
        super.setSize(width, height);
        validate();
    }
     
    // Initialize basic objects and variables
    @Override
    public void init() {
        game = new Game();
        
        //Set up the applet
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        try {
            java.awt.EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    initComponents();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Set up the game board and menus on it
    private void initComponents() {
        JPanel mainPanel = new JPanel();
        JPanel rightPanel = new JPanel();
        JPanel menuPanel = new JPanel();
        JPanel currentPlayerPanel = new JPanel();
        JPanel pieceNoPanel = new JPanel();
        Font pieceNoFont = new Font("Dialog", 0, 20);
        GridLayout menuPanelLayout = new java.awt.GridLayout(3, 1, 16, 6);
        GridLayout bottomRightPanelLayout = new java.awt.GridLayout(2, 1, 5, 5);
        Dimension diffSliderSize = new Dimension(10, 10);
        int appletWidth = 600;
        int appletHeight = 540;
        boardPanel = new BoardLayeredPane();

        // Set up the number of pieces display
        redPlayerNoPieces = new JLabel();
        bluePlayerNoPieces = new JLabel();

        updatePieceNoDisplay();
        redPlayerNoPieces.setIcon(redChecker);
        redPlayerNoPieces.setFont(pieceNoFont);
        bluePlayerNoPieces.setIcon(blueChecker);
        bluePlayerNoPieces.setFont(pieceNoFont);

        // Set up the current player display
        currentPlayerLabel = new JLabel();
        currentPlayerLabel.setIcon(blueChecker);

        // Set up layout for panels
        mainPanel.setLayout(new java.awt.BorderLayout());
        mainPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        rightPanel.setLayout(new java.awt.BorderLayout());

        menuPanel.setLayout(menuPanelLayout);
        menuPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, null, java.awt.Color.black, java.awt.Color.black));

        pieceNoPanel.setLayout(bottomRightPanelLayout);

        // Set up the "new game" button
        JButton restartGameButton = new javax.swing.JButton();
        restartGameButton.setText("New Game");
        restartGameButton.addActionListener(new NewGameListener());
        
        // Set up the "swap" button
        JButton swapCurrentPlayer = new javax.swing.JButton();
        swapCurrentPlayer.setText("Swap Player");
        swapCurrentPlayer.addActionListener(new SwapPlayerListener());
        
        // Set up the "difficulty" slider
        difficultySlider  = new JSlider(JSlider.VERTICAL, Game.EASY_AI, Game.HARD_AI, Game.EASY_AI);
        difficultySlider.setPreferredSize(diffSliderSize);
        difficultySlider.setSnapToTicks(true);

        difficultySlider.addChangeListener(new javax.swing.event.ChangeListener() {

            @Override
            public void stateChanged(javax.swing.event.ChangeEvent ce) {
                JSlider source = (JSlider) ce.getSource();
                int value = source.getValue();
                switch (value) {                     
                    case Game.EASY_AI:
                        easyLevel.setSelected(true);
                        break;
                    case Game.MEDIUM_AI:
                        mediumLevel.setSelected(true);
                        break;
                    case Game.HARD_AI:
                        hardLevel.setSelected(true);
                        break;
                }
                game.setAI(value);
                boardPanel.refreshBoardWithBorders(true);
                boardPanel.paintAttackBorders();
            }
        });

        // Set up the labels for the difficulty slider
        Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
        labelTable.put(new Integer(Game.EASY_AI), new JLabel("Easy"));
        labelTable.put(new Integer(Game.MEDIUM_AI), new JLabel("Medium"));
        labelTable.put(new Integer(Game.HARD_AI), new JLabel("Hard"));
        
        difficultySlider.setLabelTable(labelTable);
        difficultySlider.setPaintLabels(true);

        // Add contents to the main panel
        mainPanel.add(boardPanel, java.awt.BorderLayout.CENTER);
        mainPanel.add(rightPanel, java.awt.BorderLayout.LINE_END);
        
        // Add contents to the right panel
        rightPanel.add(currentPlayerPanel, java.awt.BorderLayout.PAGE_START);
        rightPanel.add(menuPanel, java.awt.BorderLayout.CENTER);
        rightPanel.add(pieceNoPanel, java.awt.BorderLayout.PAGE_END);
        
        // Set up the current player icon
        currentPlayerPanel.add(currentPlayerLabel, java.awt.BorderLayout.CENTER);
        
        // Formulate the menu panel
        menuPanel.add(restartGameButton);
        menuPanel.add(swapCurrentPlayer);
        menuPanel.add(difficultySlider);
        
        // Set up the number of pieces display
        pieceNoPanel.add(redPlayerNoPieces);
        pieceNoPanel.add(bluePlayerNoPieces);

        // Set up size of the applet
        setSize(appletWidth, appletHeight);

        // Set up layout for the background panel
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());

        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 604, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(0, 478, Short.MAX_VALUE)));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 506, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(0, 467, Short.MAX_VALUE)));

        //Create difficulty menu
        createDiffMenus();
    }
    
    //Assign values and allocate action listeners to difficultySlider
    public void createDiffMenus() {

        // Hard 
        hardLevel = new JRadioButtonMenuItem("Hard");
        hardLevel.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                game.setAI(Game.HARD_AI);
                difficultySlider.setValue(Game.HARD_AI);
                boardPanel.refreshBoardWithBorders(true);
                boardPanel.paintAttackBorders();
            }
        });

        // Medium 
        mediumLevel = new JRadioButtonMenuItem("Medium");
        mediumLevel.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                game.setAI(Game.MEDIUM_AI);
                difficultySlider.setValue(Game.MEDIUM_AI);
                boardPanel.refreshBoardWithBorders(true);
                boardPanel.paintAttackBorders();
            }
        });

        // Easy
        easyLevel = new JRadioButtonMenuItem("Easy");
        easyLevel.setSelected(true);
        easyLevel.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                game.setAI(Game.EASY_AI);
                difficultySlider.setValue(Game.EASY_AI);
                boardPanel.refreshBoardWithBorders(true);
                boardPanel.paintAttackBorders();
            }
        });
    }

    // Set up a JLabel with knowledge of its position on board
    public class PositionedLabel extends JLabel {
		private static final long serialVersionUID = 1L;
        private int position;

        PositionedLabel(Icon image, int horizontalAlignment, int argPosition) {
            super(image, horizontalAlignment);
            position = argPosition;
        }

        public int getPos() {
            return position;
        }

        public void setPos(int argPos) {
            position = argPos;
        }
    }

    // Update the number of piece left
    private void updatePieceNoDisplay() {
        redPlayerNoPieces.setText("" + game.noCompPieces());
        bluePlayerNoPieces.setText("" + game.noHumanPieces());
    }

    // Set current player icon and winner icon when the game ends
    private void setCurrentPlayerIcon() {
        if (winner == 0) {
            currentPlayerLabel.setIcon(blueChecker);
            displayWinnerMessage(Piece.HUMAN_PLAYER);
        } else if (winner == 1) {
            currentPlayerLabel.setIcon(redChecker);
            displayWinnerMessage(Piece.COMPUTER_PLAYER);  
        } else if (winner == 2) {
            currentPlayerLabel.setIcon(null);
            displayWinnerMessage(Piece.TIE);  
        }else if (currentPlayer == Piece.HUMAN_PLAYER) {
            currentPlayerLabel.setIcon(blueChecker);
        } else if (currentPlayer == Piece.COMPUTER_PLAYER) {
            currentPlayerLabel.setIcon(redChecker);
        }
    }

    // Display a message when the game ends
    private void displayWinnerMessage(int winner) {
        if (displayedWinner == true) {
            return;
        }
        
        String title = "The game has ended!";
        String message;         
        ImageIcon winnerImage;
        
        // Message contents when the game ends
        if (winner == Piece.HUMAN_PLAYER) {
            message = "BLUE won the game.";
            winnerImage = blueChecker;
        } else if (winner == Piece.COMPUTER_PLAYER) {
            message = "RED won the game.";
            winnerImage = redChecker;
        } else {
        	    message = "TIE";
        	    winnerImage = null;
        }
        
        // Display the message 
        JOptionPane.showMessageDialog(getContentPane(), message, title, JOptionPane.INFORMATION_MESSAGE, winnerImage);
        
        // Flag for the message already displayed
        displayedWinner = true;
    }

    // A layered panel for the checkers board
    public class BoardLayeredPane extends JLayeredPane {
		private static final long serialVersionUID = 1L;
        private static final int BOARD_WIDTH = 500;
        private static final int GRID_ROWS = 6;
        private static final int GRID_COLS = 6;
        private GridLayout gridlayout = new GridLayout(GRID_ROWS, GRID_COLS);
        private JPanel squarePanel = new JPanel(gridlayout);
        private JPanel[] squares = new JPanel[Game.BOARD_SIZE];
        private final Color darkSquare = new Color(85, 107, 47);
        private final Color brightSquare = new Color(194, 178, 128);

        // Initialize basic objects and variables
        public BoardLayeredPane() {
            squarePanel.setSize(new Dimension(BOARD_WIDTH, BOARD_WIDTH));
            squarePanel.setBackground(Color.black);
            for (int i = 0; i < Game.BOARD_SIZE; i++) {
                squares[i] = new JPanel(new GridBagLayout());
                squares[i].setBackground(darkSquare);
                if (!Game.VALID_SQUARE[i]) {
                    squares[i].setBackground(brightSquare);
                } else if (game.getOwnerAt(i) == 0) {
                    squares[i].add(new PositionedLabel(blueChecker, SwingConstants.CENTER, i));
                } else if (game.getOwnerAt(i) == 1) {
                    squares[i].add(new PositionedLabel(redChecker, SwingConstants.CENTER, i));
                }
                squarePanel.add(squares[i]);
            }
            add(squarePanel, JLayeredPane.DEFAULT_LAYER);
            
            // Add mouse listeners for drag and drop
            BoardMouseAdapter myMouseAdapter = new BoardMouseAdapter();
            addMouseListener(myMouseAdapter);
            addMouseMotionListener(myMouseAdapter);
        }

        // Repaint the board based on the status of the game
        public void refreshBoardWithBorders(boolean borders) {
            for (int i = 0; i < Game.BOARD_SIZE; i++) {
                squares[i].removeAll();
                if(borders)
                    squares[i].setBorder(javax.swing.BorderFactory.createEmptyBorder());

                if (!Game.VALID_SQUARE[i]) {
                    squares[i].setBackground(brightSquare);
                } 
                
                else if (game.getOwnerAt(i) == Piece.HUMAN_PLAYER) {
                    squares[i].add(new PositionedLabel(blueChecker, SwingConstants.CENTER, i));
                } 
                
                else if (game.getOwnerAt(i) == Piece.COMPUTER_PLAYER) {
                    squares[i].add(new PositionedLabel(redChecker, SwingConstants.CENTER, i));
                } 
                
                squarePanel.add(squares[i]);
            }
            updatePieceNoDisplay();
            setCurrentPlayerIcon();
            validate();
            repaint();
        }

        // Set up a mouse adapter for the board that enables Drag and Drop
        private class BoardMouseAdapter extends MouseAdapter {
            private PositionedLabel draggedLabel;
            private JPanel clickedPanel;
            private int labelMiddle;
            private int originalLabelPos = -1;
            
            @Override
            public void mousePressed(MouseEvent e) {
            
            	// Check if the game has ended
        		String Human_Moves = game.listAllMoves(Piece.HUMAN_PLAYER);
            
            if (game.noHumanPieces == 0) {
                winner = 1;
        			setCurrentPlayerIcon();
        			validate();
        			repaint();
                return;
            } 
            
            if (game.noCompPieces == 0) {
            		winner = 0;
            		setCurrentPlayerIcon();
        			validate();
        			repaint();
            		return;
            }
            
            if(Human_Moves.length() == 0) {
            		String AI_Moves = game.listAllMoves(Piece.COMPUTER_PLAYER);
            		if(AI_Moves.length() == 0) {
            			if(game.noHumanPieces > game.noCompPieces) winner =  0;
                		if(game.noHumanPieces < game.noCompPieces) winner =  1;
                		if(game.noHumanPieces == game.noCompPieces) winner =  2;
                		setCurrentPlayerIcon();
            			validate();
            			repaint();
            			return;
            		}
            		currentPlayer = (++currentPlayer) % 2;
            		AIMove();
            		setCurrentPlayerIcon();
        			validate();
        			repaint();
            		return;
            }
            
                clickedPanel = (JPanel) squarePanel.getComponentAt(e.getPoint());

                // If the clicked panel has no labels
                if (!(clickedPanel instanceof JPanel) ||clickedPanel.getComponentCount() == 0) {
                    return;
                }

                // Retrieve item from clicked panel, check if it's a label
                Component clickedComp = clickedPanel.getComponent(0);
                       
                if (!(clickedComp instanceof PositionedLabel)){
                    return;
                } 
                
                // Retrieve the label
                draggedLabel = (PositionedLabel) clickedComp;
                originalLabelPos = draggedLabel.getPos();

                // Check owner of the picked up piece/label
                if (game.getOwnerAt(originalLabelPos) != currentPlayer) {
                    return;
                }

                // Remove the label from the board
                clickedPanel.remove(draggedLabel);

                // Position the label on the mouse cursor
                labelMiddle = draggedLabel.getWidth() / 2;
                int x = e.getPoint().x - labelMiddle;
                int y = e.getPoint().y - labelMiddle;                
                draggedLabel.setLocation(x, y);
                
                // Add the label to the drag layer
                add(draggedLabel, JLayeredPane.DRAG_LAYER);
                
                validate();
                repaint();
            }


            // Update the position of the label as mouse cursor moves
            @Override
            public void mouseDragged(MouseEvent e) {
                if (draggedLabel == null
                        || game.getOwnerAt(originalLabelPos) != currentPlayer) {
                    return;
                }
                
                int x = e.getPoint().x - labelMiddle;
                int y = e.getPoint().y - labelMiddle;
                draggedLabel.setLocation(x, y);
                repaint();
            }

            // Act when the label is dropped
            @Override
            public void mouseReleased(MouseEvent e) {

                // If no label, or incorrect owner of the piece, quit
                if (draggedLabel == null
                        || game.getOwnerAt(originalLabelPos) != currentPlayer) {
                    return;
                }
                
                // Remove dragLabel from the drag layer
                remove(draggedLabel);
                draggedLabel = null;                

                // Locate where the label was dropped
                JPanel droppedPanel = (JPanel) squarePanel.getComponentAt(e.getPoint());

                // Find the position of the dropped item, return -1 if off the game board 
                int droppedPos = -1;
                for (int i = 0; i < squares.length; i++) {
                    if (squares[i] == droppedPanel) {
                        droppedPos = i;
                        break;
                    }
                }

                // If the move was invalid put label in original position
                if (droppedPos == -1 || !game.movePiece(originalLabelPos, droppedPos, currentPlayer)) {
                    boardPanel.refreshBoardWithBorders(false);
                    return;
                }
                
                boardPanel.refreshBoardWithBorders(true);               

                updatePieceNoDisplay();
                validate();
                repaint();
                
                currentPlayer = (++currentPlayer) % 2;

                // Move AI, if any active
                AIMove();

                setCurrentPlayerIcon();
                
                // Paint capturing borders
                paintAttackBorders();
            }
        }

        // Perform the appropriate AI move
        public void AIMove() {
            
            while (currentPlayer == Piece.COMPUTER_PLAYER) {
                
                // Check if the game is ended
            		String AI_Moves = game.listAllMoves(Piece.COMPUTER_PLAYER);
                
                if (game.noHumanPieces == 0) {
                    winner = 1;
                    setCurrentPlayerIcon();
                    validate();
                    repaint();
                    break;
                } 
                
                if (game.noCompPieces == 0) {
                    setCurrentPlayerIcon();
                    validate();
                    repaint();
                    break;
                }
                if(AI_Moves.length() == 0) {
                		String Human_Moves = game.listAllMoves(Piece.HUMAN_PLAYER);
                		if(Human_Moves.length() == 0) {
                			if(game.noHumanPieces > game.noCompPieces) winner =  0;
                    		if(game.noHumanPieces < game.noCompPieces) winner = 1;
                    		if(game.noHumanPieces == game.noCompPieces) winner = 2;
                            setCurrentPlayerIcon();
                            validate();
                            repaint();
                			break;
                		}
                		currentPlayer = (++currentPlayer) % 2;
                     break;
                }
                
                // Get AI move
                String aiMove = game.AIMove();
                            
                // Process the AI move
                String[] split = aiMove.split(" ");                
                
                int source = Integer.parseInt(split[0]);
                int destination = Integer.parseInt(split[1]);

                // Carry out the move, update the displayed GUI
                game.movePiece(source, destination, currentPlayer);
                
                boardPanel.refreshBoardWithBorders(false);               

                // Set up borders around the AI move
                squares[source].setBorder(BorderFactory.createLineBorder(Color.RED));
                squares[destination].setBorder(BorderFactory.createLineBorder(Color.RED));
                
                currentPlayer = (++currentPlayer) % 2;
                updatePieceNoDisplay();
                repaint();
            }
        }

        // Paints borders around pieces that must capture pieces
        private void paintAttackBorders() {
        		// List all valid attacks
            String validAttacks = game.listCaptures(currentPlayer);
            
            if (validAttacks.length() != 0) {
                String[] splitStr = validAttacks.split(" ");
                for (int i = 0; i < splitStr.length; i+=2) {
                    int sourceSqaure = Integer.parseInt(splitStr[i]);
                    int targetSqaure = Integer.parseInt(splitStr[i+1]);
                    squares[sourceSqaure].setBorder(BorderFactory.createLineBorder(Color.GREEN));
                    squares[targetSqaure].setBorder(BorderFactory.createLineBorder(Color.YELLOW));
                }
            }
        }
    }

    // Below is a list of  action listeners
    
    // A listener for the new game button 
    class NewGameListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int tempAI = game.AIType();
            game = new Game();
            winner = -1;
            currentPlayer = 0;
            boardPanel.refreshBoardWithBorders(true);
            game.setAI(tempAI);
            displayedWinner = false;
        }
    }
    
    // A listener for the swap player button 
    class SwapPlayerListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            currentPlayer = (currentPlayer + 1) % 2;
            boardPanel.AIMove();
            boardPanel.refreshBoardWithBorders(true);
            boardPanel.paintAttackBorders();
            validate();
            repaint();
        }
    }

    // A listener for the game type selection button 
    class GameTypeButtonListener implements ActionListener {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            game = new Game();
            game.setAI(Game.EASY_AI);
            difficultySlider.setValue(Game.EASY_AI);
            easyLevel.setSelected(true);
            difficultySlider.setVisible(true);
            currentPlayer = 0;
            boardPanel.refreshBoardWithBorders(true);
            displayedWinner = false;
        }
    }
}