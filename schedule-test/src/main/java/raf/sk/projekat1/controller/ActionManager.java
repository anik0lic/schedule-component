package raf.sk.projekat1.controller;

import lombok.Getter;
import lombok.Setter;
import raf.sk.projekat1.controller.actions.InfoCSVAction;
import raf.sk.projekat1.controller.actions.InfoJSONAction;

@Getter
@Setter
public class ActionManager {

    private InfoCSVAction infoCSVAction;
    private InfoJSONAction infoJSONAction;

    public ActionManager(){
        infoCSVAction = new InfoCSVAction();
        infoJSONAction = new InfoJSONAction();
    }

}
