package com.pardot.rhombus.cli.commands;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

/**
 * Rob Righter
 * Date: 8/17/13
 */
public class RebuildKeyspace  extends RcliWithCassandraConfig {

    public Options getCommandOptions(){
        Options ret = super.getCommandOptions();
        Option forceRebuild = new Option( "f", "Force destruction and rebuild of keyspace" );
        ret.addOption(forceRebuild);
        return ret;
    }

    public void executeCommand(CommandLine cl){
        super.executeCommand(cl);

        //now rebuild the keyspace
        try{

            getConnectionManager().buildKeyspace(keyspaceDefinition,cl.hasOption("f"));
            //looks like a success. Lets go ahead and return as such
            System.exit(0);
        }
        catch (Exception e){
            System.out.println("Error encountered while attempting to rebuild the keyspace");
        }

    }
}
