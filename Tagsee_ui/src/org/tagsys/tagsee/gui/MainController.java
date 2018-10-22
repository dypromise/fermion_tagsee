package org.tagsys.tagsee.gui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import com.impinj.octane.*;

//import com.sun.org.apache.bcel.internal.generic.NEW;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import org.tagsys.tagsee.utils.*;


public class MainController extends AnchorPane implements TagReportListener {

    public static int tags_lenth = FilterUtils.tags_len;
    private static long delay_time = 1000;
    private static long cnt = 0;
    private static int times = 1000;
    public static JMSProducer producer = new JMSProducer();
    //BufferedWriter[] bw = new BufferedWriter[tags_lenth];
    public static double[] values = new double[tags_lenth *2];
    private static double[] pre_angle = new double[tags_lenth];
    private static double[] pre_rssi = new double[tags_lenth];

    private static int[] k = new int[tags_lenth];

    private static double[] tagsee_values = new double[tags_lenth];


    private File file_ = new File("/Users/dingyang/tagsee_data_3.txt");
    private BufferedWriter bw = new BufferedWriter(new FileWriter(file_));


    @FXML
    Hyperlink moreInfoLink;
    @FXML
    Label errorMessage;
    @FXML
    TextField ipText;
    @FXML
    TextField timeText;
    @FXML
    TextField directoryText;
    @FXML
    ListView<String> freqList;
    @FXML
    ComboBox epcCombo;
    @FXML
    TableView dataTable;
    @FXML
    TableView resultTable;
    @FXML
    Button collectButton;
    @FXML
    Button annalyseButton;


    private void write_to_txt() throws IOException {
        StringBuffer stringBuffer = new StringBuffer();
        for(double i : tagsee_values){
            stringBuffer.append(Double.toString(i)+",");
        }
        stringBuffer.append('\n');
        String line = stringBuffer.toString();
        bw.write(line);
    }

    public MainController() throws IOException {
    }

    @Override
    public void onTagReported(ImpinjReader reader, TagReport report) {
        List<Tag> tags = report.getTags();

        cnt += 1;
        if (cnt >= 100) {
            if (cnt < times + 100) {}
            else {
                for (Tag t : tags) {
                    String epc_ = t.getEpc().toHexString();
                    if (!FilterUtils.isWantedTag(epc_))
                        continue;

                    double alpha, A1, move_signal_ang = 0, beta, B1;
                    int index = Integer.parseInt(epc_.substring(20, 24)) - 1;
                    if (t.isRfPhaseAnglePresent()) {
                        values[index] = t.getPhaseAngleInRadians();
                        tagsee_values[index] = t.getPhaseAngleInRadians();
                    }
                }
                try {
                    write_to_txt();
                    for(int i = 0;i<tags_lenth;++i){
                        tagsee_values[i] = 0;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }


    @FXML
    protected void processItemChanged(ActionEvent event) {
        String epc = (String) this.epcCombo.getSelectionModel()
                .getSelectedItem();
    }

    @FXML
    protected void processCollecteData(ActionEvent event) {

        new Thread() {

            public void run() {


                try {

                    MainController mc = new MainController();

                    String hostname = "192.168.1.212";
                    delay_time =30000;

                    ImpinjReader reader = new ImpinjReader();

                    System.out.println("Connecting");
                    reader.connect(hostname);

                    Settings settings = reader.queryDefaultSettings();

                    // Dingyang add.
                    settings.getReport().setIncludePeakRssi(true);
                    settings.getReport().setIncludePhaseAngle(true);

                    ReportConfig report = settings.getReport();
                    report.setIncludeAntennaPortNumber(true);
                    report.setMode(ReportMode.Individual);

                    // The reader can be set into various modes in which reader
                    // dynamics are optimized for specific regions and environments.
                    // The following mode, AutoSetDenseReader, monitors RF noise and interference and then automatically
                    // and continuously optimizes the reader's configuration
                    settings.setReaderMode(ReaderMode.AutoSetDenseReader);

                    // set some special settings for antenna 1
                    AntennaConfigGroup antennas = settings.getAntennas();
                    antennas.disableAll();
                    antennas.enableById(new short[]{1});
                    antennas.getAntenna((short) 1).setIsMaxRxSensitivity(false);
                    antennas.getAntenna((short) 1).setIsMaxTxPower(false);
                    antennas.getAntenna((short) 1).setTxPowerinDbm(20.0);
                    antennas.getAntenna((short) 1).setRxSensitivityinDbm(-70);

                    reader.setTagReportListener(mc);

                    System.out.println("Applying Settings");
                    reader.applySettings(settings);

                    System.out.println("Starting");
                    reader.start();

                    Transmit_thread transmit_thread = new Transmit_thread();
                    transmit_thread.start();
                    Thread.sleep(delay_time);
                    transmit_thread.stop();


                    reader.stop();
                    reader.disconnect();
                    bw.close();

                } catch (OctaneSdkException ex) {
                    System.out.println(ex.getMessage());
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    ex.printStackTrace(System.out);
                }

            }

        }.start();

    }

    @FXML
    protected void processAnnalyse(ActionEvent event) {

    }

    @FXML
    protected void processMoreInfo(ActionEvent event) {
        this.errorMessage.setText("No more infomation for PANDA LOCALIZATION");
    }

    @FXML
    protected void processExportData(ActionEvent event) {


//        try {
//            String dir = this.directoryText.getText();
//
//            File file = new File(dir);
//            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
//            writer.write("epc, preciseRssi, phase, doppler, time");
//            writer.newLine();
//            for (String epc : data.keySet()) {
//                List<Observation> dataList = data.get(epc);
//                for (Observation obs : dataList) {
//                    writer.write(epc);
//                    // writer.write("," + obs.getPeekRssi());
//                    writer.write("," + obs.getPreciseRssi());
//                    writer.write("," + obs.getPhase());
////					writer.write("," + obs.getDoppler());
////					writer.write("," + obs.getTimestamp());
//                    writer.newLine();
//                }
//                writer.flush();
//            }
//            writer.close();
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }


}