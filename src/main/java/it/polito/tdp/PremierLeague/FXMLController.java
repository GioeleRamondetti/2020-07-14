/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.PremierLeague;

import java.net.URL;
import java.util.ResourceBundle;

import it.polito.tdp.PremierLeague.model.Model;
import it.polito.tdp.PremierLeague.model.Team;
import it.polito.tdp.PremierLeague.model.reporter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {
	
	private Model model;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="btnCreaGrafo"
    private Button btnCreaGrafo; // Value injected by FXMLLoader

    @FXML // fx:id="btnClassifica"
    private Button btnClassifica; // Value injected by FXMLLoader

    @FXML // fx:id="btnSimula"
    private Button btnSimula; // Value injected by FXMLLoader

    @FXML // fx:id="cmbSquadra"
    private ComboBox<Team> cmbSquadra; // Value injected by FXMLLoader

    @FXML // fx:id="txtN"
    private TextField txtN; // Value injected by FXMLLoader

    @FXML // fx:id="txtX"
    private TextField txtX; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML
    void doClassifica(ActionEvent event) {
    	if(cmbSquadra.getValue()!=null) {
    		model.cercamigliori(cmbSquadra.getValue());
    		txtResult.setText("migliori \n");
    		for(String i:model.getOrdineMigliori().keySet()) {
    			txtResult.appendText(i+"\n");
    		}
    		txtResult.appendText("\n peggiori \n");
    		for(String i:model.getOrdinePeggiori().keySet()) {
    			txtResult.appendText(i+"\n");
    		}
    	}
    }

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	model.creagrafo();
    	txtResult.setText("grafo creato con archi: "+model.getNarchi()+" vertici: "+model.getNverici());
    }

    @FXML
    void doSimula(ActionEvent event) {
    	if(!txtN.getText().equals("") && !txtX.getText().equals("")) {
    		model.simula(Integer.parseInt(txtN.getText()),Integer.parseInt(txtX.getText()));
    		txtResult.setText("reporter sotto soglia "+model.getContacritico()+"\n");
    		txtResult.appendText("reporter medi per squadra \n");
    		for(reporter r :model.getReporter()) {
    			int n=0;
    			for(int i=0;i<r.getListaRep().size();i++) {
    				n=n+r.getListaRep().get(i);
    			}
    			txtResult.appendText(r.getT().getName()+" reported medi "+n/r.getListaRep().size()+"\n");
    		}
    	}
    	
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnClassifica != null : "fx:id=\"btnClassifica\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnSimula != null : "fx:id=\"btnSimula\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbSquadra != null : "fx:id=\"cmbSquadra\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtN != null : "fx:id=\"txtN\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtX != null : "fx:id=\"txtX\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";
    }
    
    public void setModel(Model model) {
    	this.model = model;
    	cmbSquadra.getItems().addAll(model.squadre());
    }
}
