/**
 * This project is made by Vincenzo Petrolo on April 2020
 */
package com.example.mqttjoystickcontroller;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;

import mqtt_controller.application.R;

public class ControllerActivity extends Activity {

    /**
     * All the objects on the screen
     */
    private Button connectButton;
    private Button stopButton;
    private EditText brokerAddress;
    private EditText topic;
    private TextView connectionStatus;
//    private EditText username;
//    private EditText password;
    private Button liftupButton;
    private Button liftdownButton;

    protected MQTTManager mqttManager;
    private JoystickClass joystickClass;

    /**
     * This method is called at the creation of the main activity (when you open the application)
     * all the object for example connectButton can be found in the res (Directory) and then layour
     * there you can find the main view of the application
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.connectButton  = (Button) findViewById(R.id.connectButton);
        this.stopButton     = (Button) findViewById(R.id.emergencyButton);
        this.brokerAddress  = (EditText) findViewById(R.id.brokerText);
        this.topic          = (EditText) findViewById(R.id.topicText);
        this.connectionStatus = (TextView) findViewById(R.id.connectionStatus);
//        this.username       = (EditText) findViewById(R.id.username);
//        this.password       = (EditText) findViewById(R.id.password);

        this.liftupButton   = (Button) findViewById(R.id.liftupButton);
        this.liftdownButton = (Button) findViewById(R.id.liftdownButton);

        this.mqttManager    = new MQTTManager(this.brokerAddress.getText().toString(),this);
        this.joystickClass  = new JoystickClass(this);


        final ControllerActivity activity         = this;
        final TextView tmpStatus        = this.connectionStatus;

        this.connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!activity.brokerAddress.getText().toString().isEmpty()){
                    mqttManager = new MQTTManager(brokerAddress.getText().toString(), activity);
                }else {
                    mqttManager = new MQTTManager(activity);
                    activity.brokerAddress.setText(mqttManager.getUserBrokerAddress());
                }
//                if (!activity.username.getText().toString().isEmpty() && !activity.password.getText().toString().isEmpty()){
//                    mqttManager.getMqttConnectOptions().setUserName(getUsername());
//                    mqttManager.getMqttConnectOptions().setPassword(getPassword().toCharArray());
//                }
                try {
                    mqttManager.getMqttClient().connect(mqttManager.getMqttConnectOptions(), null, new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken iMqttToken) {
                            tmpStatus.setText("Connected");
                            tmpStatus.setTextColor(Color.GREEN);
                        }

                        @Override
                        public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                            tmpStatus.setText("Failed");
                            tmpStatus.setTextColor(Color.RED);
                            throwable.printStackTrace();
                            System.out.println(throwable.getMessage());
                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });

        this.stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mqttManager.sendMessage("STOP","STOP");
            }
        });

        this.liftupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mqttManager.sendMessage("up", "/home/car/lift");
            }
        });
        this.liftdownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mqttManager.sendMessage("down", "/home/car/lift");
            }
        });
    }

    /**
     *
     * @return the text in the username field
     */
//    public String getUsername(){
//        return this.username.getText().toString();
//    }

    /**
     *
     * @return the password in the password field
     */
//    public String getPassword(){
//        return this.password.getText().toString();
//    }

    /**
     * Getter of the MqttManager object
     * @return an MqttManager object
     */
    public MQTTManager  getMqttManager(){
        return this.mqttManager;
    }

    /**
     *
     * @return the text written in topic when one of the two joysticks is moved
     */
    public String getTopic() {
        return this.topic.getText().toString();
    }
}
