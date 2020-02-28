package com.fpackage.common;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//Update
import com.fpackage.connections.Client_CLIConnection;
import com.mks.api.Command;
import com.mks.api.Option;
import com.mks.api.response.APIException;
import com.mks.api.response.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;

/**
 *
 * @author gauravk1
 */
public class Main {

    public static void main(String args[]) {

      InputContents.getContents();
      InputContents.commandTest();
    }
}
