import java.io.*;


public class Assembler {

    public static void main(String[] args) throws IOException{
        if(args.length == 1 && args[0].toLowerCase().equals("help"))
        {
            System.out.println("java Assembler [Source] [Destination]");
            System.out.println("Run without arguments to convert every .asm file in the working directory to .hack");
            System.out.println("Run with just source that is the path to the .asm file and it will convert to a .hack file in the same path with the same name");
            System.out.println("Run with source and destination and it will convert the .asm source to a .hack destination");
            System.exit(0);
        }
        if(args.length == 0)
        {
            //Look for .asm files in working directory and convert them. If none found then print error and exit
            boolean foundASM = false;
            File workDir = new File(System.getProperty("user.dir"));
            File[] fileList = workDir.listFiles();
            for(File opFile : fileList)
            {
                if(opFile.isFile() && opFile.getName().endsWith(".asm"))
                {
                    foundASM = true;
                    Parser parse = new Parser(opFile.getName());
                    File file = new File(opFile.getName().substring(0, opFile.getName().length() - 4) + ".hack");
                    parse.assemble(file);
                }
            }

            if(!foundASM)//No .asm in working directory, exit
                System.out.println("No args given, exiting");
            System.exit(0);
        }

        Parser parse = new Parser(args[0]);
        File file = new File(args.length == 1 ? args[0].substring(0, args[0].length() - 4) + ".hack" : args[1]);
        parse.assemble(file);
    }


}
