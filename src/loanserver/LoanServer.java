/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loanserver;

/**
 *
 * @author Diego
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import java.util.Date;
import loan.Loan;

public class LoanServer extends JFrame{

    /**
     * @param args the command line arguments
     */
    JTextArea jta = new JTextArea();
    
    public LoanServer(){
        setLayout(new BorderLayout());
        add(new JScrollPane(jta), BorderLayout.CENTER);
        
        setTitle("Loan Server");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        
        try{
            ServerSocket serverSocket = new ServerSocket(8000);
            jta.append("Server started at " + new Date() + "\n");
            
            int clientID = 1;
            
            while(true){
                Socket socket = serverSocket.accept();
                
                Runnable clientThread = new ClientHandler(socket, clientID);
                
                new Thread(clientThread).start();
                clientID = clientID + 1;
                
            }
            
            
        }catch(IOException ex){
            System.err.println(ex);
        }
    }
    
    private class ClientHandler implements Runnable{
        Socket clientSocket;
        int clientID;
        
        public ClientHandler(Socket clientSocket, int clientID){
            this.clientSocket = clientSocket;
            this.clientID = clientID;
        }
        
        @Override
        public void run(){
            try{
                DataInputStream inputFromClient = new DataInputStream( clientSocket.getInputStream() );
                DataOutputStream outputToClient = new DataOutputStream( clientSocket.getOutputStream() );
                
                while(true){
                    double annualInterestRate = inputFromClient.readDouble();
                    int numberOfYears = inputFromClient.readInt();
                    double loanAmount = inputFromClient.readDouble();
                    
                    Loan clientLoan = new Loan( annualInterestRate, numberOfYears, loanAmount);
                    synchronized(jta){
                        jta.append("Client " + clientID + ": host name is " + clientSocket.getInetAddress().getHostName() + "\n");
                        jta.append("Client " + clientID + ": IP Addres is " + clientSocket.getInetAddress().getHostAddress() + "\n");
                
                        jta.append("Client " + clientID + ":Annual Interest Rate: " + clientLoan.getAnnualInterestRate() + "\n");
                        jta.append("Client " + clientID + ":Number Of Years: " + clientLoan.getNumberOfYears() + "\n");
                        jta.append("Client " + clientID + ":Loan Amount: " + clientLoan.getLoanAmount() + "\n");
                        jta.append("Client " + clientID + ":Monthly Payment: " + clientLoan.getMonthlyPayment() + "\n");
                        jta.append("Client " + clientID + ":Total Payment: " + clientLoan.getTotalPayment() + "\n");
                    }
                    
                    
                    outputToClient.writeDouble( clientLoan.getMonthlyPayment() );
                    outputToClient.writeDouble( clientLoan.getTotalPayment() );
                
                }
            }catch(IOException ex){
                
            }
        }
    }
    
    public static void main(String[] args) {
        // TODO code application logic here
        new LoanServer();
    }
    
}
