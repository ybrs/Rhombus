package com.pardot.rhombus.cli.commands;

import com.pardot.rhombus.CassandraConfiguration;
import com.pardot.rhombus.ConnectionManager;
import com.pardot.rhombus.cli.RhombusCli;
import com.pardot.rhombus.cli.RhombusCommand;
import com.pardot.rhombus.cobject.CKeyspaceDefinition;
import com.pardot.rhombus.util.JsonUtil;
import org.apache.commons.cli.*;

import java.io.IOException;

/**
 * User: Rob Righter
 * Date: 8/17/13
 */
public class RcliWithCassandraConfig extends RhombusCli {

    private ConnectionManager connectionManager = null;

    public Options getCommandOptions(){
        Options ret = super.getCommandOptions();
        Option cassConfig = OptionBuilder.withArgName( "filename" )
                .hasArg()
                .withDescription("Filename of json Cassandra Configuration")
                .create( "cassconfig" );
        ret.addOption(cassConfig);
        return ret;
    }

    public ConnectionManager getConnectionManager(){
        return connectionManager;
    }

    public void executeCommand(CommandLine cl){
        super.executeCommand(cl);

        if(!cl.hasOption("cassconfig")){
            displayHelpMessageAndExit();
        }

        String cassConfigFileName = cl.getOptionValue("cassconfig");
        //make the keyspace definition
        CassandraConfiguration cassConfig = null;
        try{
            cassConfig = JsonUtil.objectFromJsonFile(CassandraConfiguration.class, CassandraConfiguration.class.getClassLoader(), cassConfigFileName);
        }
        catch (Exception e){
            System.out.println("Could not parse cassandra configuration file "+cassConfigFileName);
            System.exit(1);
        }

        if(cassConfig == null){
            System.out.println("Could not parse cassandra configuration file "+cassConfigFileName);
            System.exit(1);
        }

        connectionManager = new ConnectionManager(cassConfig);

        if(connectionManager == null){
            System.out.println("Could create cassandra connection manager from file "+cassConfigFileName);
            System.exit(1);
        }

    }
}
