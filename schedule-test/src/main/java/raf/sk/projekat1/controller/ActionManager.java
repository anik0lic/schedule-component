package raf.sk.projekat1.controller;

import lombok.Getter;
import lombok.Setter;
import raf.sk.projekat1.controller.actions.*;

@Getter
@Setter
public class ActionManager {

    private InfoCSVAction infoCSVAction;
    private InfoJSONAction infoJSONAction;
    private MainFrameAction mainFrameAction;
    private ImportSchedule importSchedule;
    private ImportPlaces importPlaces;

    public ActionManager(){
        infoCSVAction = new InfoCSVAction();
        infoJSONAction = new InfoJSONAction();
        mainFrameAction = new MainFrameAction();
        importSchedule = new ImportSchedule();
        importPlaces = new ImportPlaces();
    }

}