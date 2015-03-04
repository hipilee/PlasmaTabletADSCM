package com.cylinder.www.thread;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.cylinder.www.env.CollectionFaceInfo;
import com.cylinder.www.env.JsonFace;
import com.cylinder.www.env.Mode;
import com.cylinder.www.env.Signal;
import com.cylinder.www.env.TimeInterval;
import com.cylinder.www.env.net.InterfaceNetwork;
import com.cylinder.www.env.net.InterfaceNetworkCreator;
import com.cylinder.www.env.net.ServerInformationTransaction;
import com.cylinder.www.env.net.ServerNetworkCreator;
import com.cylinder.www.env.person.businessobject.Donor;
import com.cylinder.www.utils.FaceQuality;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class SendPictureThread extends Thread {

	private InterfaceNetwork network =null;
	private InterfaceNetworkCreator NetworkCreator = null;
	private Handler handler;
    private int result;

	public SendPictureThread(Handler handler) {
		
		super();
		this.handler = handler;
        NetworkCreator = new ServerNetworkCreator();
        network = NetworkCreator.creator();
		}

	@Override
	public void run() {

        if(Mode.isDebug) {
            Log.e("error", " elapsed time" + TimeInterval.getInstance().getInterval());
        }
		if( TimeInterval.getInstance().getInterval() < 2.0){
			
			CollectionFaceInfo.getInstance().addTakenPictureNO();

            if(FaceQuality.getInstance().evaluate(CollectionFaceInfo.getInstance().getPicture(),0.5f)){
                CollectionFaceInfo.getInstance().addSendNumber();

                //  Generate the JsonFace object
                JsonFace jsonFace = new JsonFace();
                jsonFace.setAutomaticRecognition(true);
                jsonFace.setData(CollectionFaceInfo.getInstance().getPicture());
                jsonFace.setMachineId("123456");
                jsonFace.setDonorId(Donor.getInstance().getDonorID());
                jsonFace.setId(null);
                jsonFace.setLength(CollectionFaceInfo.getInstance().getPicture().length);

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("machineId",jsonFace.getMachineId());
                    jsonObject.put("length",jsonFace.getLength());
                    jsonObject.put("automaticRecognition",jsonFace.isAutomaticRecognition());
                    jsonObject.put("donorId",jsonFace.getDonorId());

                    // Put the byte array into the JSONArray.
                    JSONArray jsonArray = new JSONArray();
                    for(byte rgb:CollectionFaceInfo.getInstance().getPicture())
                        jsonArray.put(rgb);

                    jsonObject.put("data", jsonArray);

                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

                ServerInformationTransaction serverInformationTransaction = new ServerInformationTransaction();
                JSONObject jsonObjectRequest = serverInformationTransaction.sendToServer(network.getJsonURL()+"/rest/photos",jsonObject);

                try {
                    result = jsonObjectRequest.getInt("recognitionResult");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // successful verification
                if(result == 1){
                    if(Mode.isDebug){
                        Log.e("error","识别成功");
                    }
                    Message msg = Message.obtain();
                    msg.obj = Signal.ENDCAPTURE;
                    handler.sendMessage(msg);

                    // Reset the collection relative time.
                    CollectionFaceInfo.getInstance().resetSendNumber();
                    CollectionFaceInfo.getInstance().resetTakenPictureNO();
                }
                // failure verification
                else{
                    if(Mode.isDebug){
                        Log.e("error","识别失败");
                    }
                    // Step the next sending period.
                    Message msg = Message.obtain();
                    msg.obj = Signal.CAPTURE;
                    handler.sendMessage(msg);
                }
            }
            else {
                if(Mode.isDebug){
                    Log.e("error","质量不合格");
                }
                Message msg = Message.obtain();
                msg.obj = Signal.CAPTURE;
                handler.sendMessage(msg);
                return;
            }
		}
        else{

            if(Mode.isDebug){
                Log.e("error","采集超时");
            }
            Message msg = Message.obtain();
            msg.obj = Signal.LAUNCHVIDEO;
            handler.sendMessage(msg);

            // Reset the collection relative time.
            CollectionFaceInfo.getInstance().resetSendNumber();
            CollectionFaceInfo.getInstance().resetTakenPictureNO();
        }

		super.run();
	}
	

    
}
