import java.io.*;
import java.util.Scanner;
import java.util.Hashtable;

public class Parser {
    private File source;
    private Scanner scan;
    private String currComm;
    private Hashtable<String, String> symbolTable = new Hashtable<String, String>();
    private int varSymCount = 16;
    private int instrCount = 0;

    protected Parser(String fileName)
    {
        source = new File(fileName);
        try {
            scan = new Scanner(source);
        }
        catch(Exception e){
            System.out.println("File not found");
            System.exit(-1);
        }

        symbolTable.put("SP", "0");
        symbolTable.put("R0", "0");
        symbolTable.put("LCL", "1");
        symbolTable.put("R1", "1");
        symbolTable.put("ARG", "2");
        symbolTable.put("R2", "2");
        symbolTable.put("THIS", "3");
        symbolTable.put("R3", "3");
        symbolTable.put("THAT", "4");
        symbolTable.put("R4", "4");
        symbolTable.put("R5", "5");
        symbolTable.put("R6", "6");
        symbolTable.put("R7", "7");
        symbolTable.put("R8", "8");
        symbolTable.put("R9", "9");
        symbolTable.put("R10", "10");
        symbolTable.put("R11", "11");
        symbolTable.put("R12", "12");
        symbolTable.put("R13", "13");
        symbolTable.put("R14", "14");
        symbolTable.put("R15", "15");
        symbolTable.put("SCREEN", "16384");
        symbolTable.put("KBD", "24576");
    }

    private boolean hasMoreCommands()
    {
        return scan.hasNext();
    }

    private boolean advance()
    {
        if(hasMoreCommands()) {
            currComm = scan.nextLine().split("//")[0].trim();
            if(currComm.length() == 0) {
                advance();
            }
            return true;
        }
        return false;
    }

    private String commandType()
    {
        if(currComm.charAt(0) == '@')
            return "A_COMMAND";
        else if(currComm.charAt(0) == '(')
            return "L_COMMAND";
        else
            return "C_COMMAND";
    }

    private String symbol()
    {

        if(commandType().equals("A_COMMAND")) // @Mem
            return currComm.substring(1);
        else if(commandType().equals("L_COMMAND")) //(label)
            return currComm.substring(1, currComm.length() - 1);
        return null;
    }

    private String dest()
    {
        if(commandType().equals("C_COMMAND"))
            return currComm.contains("=") ? currComm.split("=")[0] : "null";
        return "null";
    }

    private String comp()
    {
        if(commandType().equals("C_COMMAND"))
            return currComm.contains("=") ? currComm.split("=")[1] : currComm.contains(";") ? currComm.split(";")[0] : "null";
        return "null";
    }

    private String jump()
    {
        if(commandType().equals("C_COMMAND"))
            return currComm.contains(";") ? currComm.split(";")[1] : "null";
        return "null";
    }

    private String codeDest()
    {
        switch(dest())
        {
            case "null": return "000";
            case "M": return "001";
            case "D": return "010";
            case "MD": return "011";
            case "A": return "100";
            case "AM": return "101";
            case "AD": return "110";
            case "AMD": return "111";
            default: return "ERROR";
        }
    }

    private String codeComp()
    {
        switch(comp())
        {
            case "0": return "0101010";
            case "1": return "0111111";
            case "-1": return "0111010";
            case "D": return "0001100";
            case "A": return "0110000";
            case "M": return "1110000";
            case "!D": return "0001101";
            case "!A": return "0110001";
            case "!M": return "1110001";
            case "-D": return "0001111";
            case "-A": return "0110011";
            case "-M": return "1110011";
            case "D+1": return "0011111";
            case "A+1": return "0110111";
            case "M+1": return "1110111";
            case "D-1": return "0001110";
            case "A-1": return "0110010";
            case "M-1": return "1110010";
            case "D+A": return "0000010";
            case "D+M": return "1000010";
            case "D-A": return "0010011";
            case "D-M": return "1010011";
            case "A-D": return "0000111";
            case "M-D": return "1000111";
            case "D&A": return "0000000";
            case "D&M": return "1000000";
            case "D|A": return "0010101";
            case "D|M": return "1010101";
            default: return "ERROR";
        }
    }

    private String codeJump()
    {
        switch(jump())
        {
            case "null": return "000";
            case "JGT": return "001";
            case "JEQ": return "010";
            case "JGE": return "011";
            case "JLT": return "100";
            case "JNE": return "101";
            case "JLE": return "110";
            case "JMP": return "111";
            default: return "ERROR";
        }
    }

    private void buildSymbolTable()
    {
        while(advance())
        {
            if(commandType().equals("L_COMMAND"))
                symbolTable.put(symbol(), Integer.toString(instrCount));
            else
                instrCount++;
        }
    }

    private boolean isNumber(String in)
    {
        for(int i = 0; i < in.length(); i++)
            if(!Character.isDigit(in.charAt(i)))
                return false;
        return true;
    }

    private String translate()
    {
        if(commandType().equals("C_COMMAND"))
        {
            return "111" + codeComp() + codeDest() + codeJump();
        }
        else if(commandType().equals("A_COMMAND"))
        {
            String bin = "";
            if(isNumber(symbol())){ //@number
                bin = symbol();
            }
            else {
                if(!symbolTable.containsKey(symbol()))  //If not in table, add it
                    symbolTable.put(symbol(), Integer.toString(varSymCount++));
                bin = symbolTable.get(symbol());
            }

            bin = Integer.toBinaryString(Integer.parseInt(bin));
            while(bin.length() < 15)
                bin = "0" + bin;
            return "0" + bin;
        }
        else //L_COMMAND
        {

        }
        return null;
    }

    public void assemble(File file) throws IOException
    {
        FileWriter writ = new FileWriter(file, false);
        buildSymbolTable();
        String binVersion = "";
        scan = new Scanner(source);
        int i = 0;
        while(advance()) {
            binVersion = translate();
            if(binVersion != null) {
                writ.append(binVersion + '\n');
            }
        }
        writ.close();
    }
}
