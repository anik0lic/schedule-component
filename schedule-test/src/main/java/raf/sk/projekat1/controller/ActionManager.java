package raf.sk.projekat1.controller;

import lombok.Getter;
import lombok.Setter;
import raf.sk.projekat1.controller.actions.InfoCSVAction;
import raf.sk.projekat1.controller.actions.InfoJSONAction;
import raf.sk.projekat1.controller.actions.MainFrameAction;

@Getter
@Setter
public class ActionManager {

    private InfoCSVAction infoCSVAction;
    private InfoJSONAction infoJSONAction;
    private MainFrameAction mainFrameAction;

    public ActionManager(){
        infoCSVAction = new InfoCSVAction();
        infoJSONAction = new InfoJSONAction();
        mainFrameAction = new MainFrameAction();
    }

}
