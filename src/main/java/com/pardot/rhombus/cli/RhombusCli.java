package com.pardot.rhombus.cli;


import com.pardot.rhombus.cobject.CKeyspaceDefinition;
import com.pardot.rhombus.util.JsonUtil;
import org.apache.commons.cli.*;

import java.io.IOException;

/**
 * User: Rob Righter
 * Date: 8/17/13
 * Time: 11:06 AM
 */
public class RhombusCli {

    public CommandLineParser commandLineParser;

    public static Options makeCommandLineOptions(){
        Options ret = new Options();
        Option help = new Option( "help", "print this message" );
        Option command = OptionBuilder.withArgName( "classname" )
                .hasArg()
                .withDescription("The Rhombus command class")
                .create( "command" );
        Option cassConfig = OptionBuilder.withArgName( "filename" )
                .hasArg()
                .withDescription("Filename of json Cassandra Configuration")
                .create( "cassconfig" );
        Option keyspaceFile = OptionBuilder.withArgName( "filename" )
                .hasArg()
                .withDescription("Filename of json keyspace definition")
                .create( "keyspacefile" );
        Option keyspaceResource = OptionBuilder.withArgName( "filename" )
                .hasArg()
                .withDescription("Filename of json keyspace definition")
                .create( "keyspaceResource" );
        ret.addOption(help);
        ret.addOption(command);
        ret.addOption(cassConfig);
        ret.addOption(keyspaceFile);
        ret.addOption(keyspaceResource);
        return ret;

    }

    public static void main( String[] args ) {
        // create the parser
        CommandLineParser parser = new org.apache.commons.cli.BasicParser();
        try {
            // parse the command line arguments
            CommandLine line = parser.parse( makeCommandLineOptions(), args );
            // make sure they gave us a command
            if( !line.hasOption( "command" ) ||
                !(line.hasOption("keyspacefile") || line.hasOption("keyspaceresource"))) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp( "RhombusCli", makeCommandLineOptions() );
                System.exit(1);
            }

            String keyspaceFileName = line.hasOption("keyspacefile") ? line.getOptionValue("keyspacefile") : line.getOptionValue("keyspaceresource");
            //make the keyspace definition
            CKeyspaceDefinition keyDef = null;
            try{
                keyDef = line.hasOption("keyspacefile") ?
                    JsonUtil.objectFromJsonFile(CKeyspaceDefinition.class,CKeyspaceDefinition.class.getClassLoader(), keyspaceFileName) :
                    JsonUtil.objectFromJsonResource(CKeyspaceDefinition.class,CKeyspaceDefinition.class.getClassLoader(), keyspaceFileName);
            }
            catch (IOException e){
                System.out.println("Could not parse keyspace file "+keyspaceFileName);
                System.exit(1);
            }

            if(keyDef == null){
                System.out.println("Could not parse keyspace file "+keyspaceFileName);
                System.exit(1);
            }

            //Load up the class
            //if the class name is not fully qualified we assume its in com.pardot.rhombus.cli.commands
            String className = line.getOptionValue("command").toString();
            if(!className.contains(".")){
                className = "com.pardot.rhombus.cli.commands."+ className;
            }

            try{
                RhombusCommand cmd = (RhombusCommand)(Class.forName(className)).newInstance();
                Options commandOptions = cmd.getCommandOptions();
                for(Object opt : makeCommandLineOptions().getOptions()){
                    commandOptions.addOption((Option)opt);
                }
                cmd.executeCommand(parser.parse( commandOptions, args ));
            }
            catch (ClassNotFoundException e){
                System.out.println("Could not find Command Class "+className);
            }
            catch (IllegalAccessException e){
                System.out.println("Could not access Command Class "+className);
            }
            catch (InstantiationException e){
                System.out.println("Could not instantiate Command Class "+className);
            }
        }
        catch( ParseException exp ) {
            // oops, something went wrong
            System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
        }
    }


}
