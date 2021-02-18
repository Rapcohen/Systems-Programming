package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/** This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {
	public static CountDownLatch initializeDoneSignal;
	public static CountDownLatch terminationDoneSignal;

	public static void main(String[] args) {
		initializeDoneSignal = new CountDownLatch(4);
		terminationDoneSignal = new CountDownLatch(5);
		try{
			Gson gson = new Gson();
			JsonReader reader = new JsonReader(new FileReader(args[0]));
			JsonTranslator translator = gson.fromJson(reader,JsonTranslator.class);

			Ewoks.getInstance().createEwoks(translator.getEwoks());

			LeiaMicroservice leiaMicroservice =  new LeiaMicroservice(translator.getAttacks());
			leiaMicroservice.setDeactivateDuration(translator.getR2D2());
			leiaMicroservice.setBombDuration(translator.getLando());
			Thread leia = new Thread(leiaMicroservice);
			Thread c3po = new Thread(new C3POMicroservice());
			Thread han = new Thread(new HanSoloMicroservice());
			Thread r2d2 = new Thread(new R2D2Microservice());
			Thread lando = new Thread(new LandoMicroservice());

			leia.start();
			han.start();
			c3po.start();
			r2d2.start();
			lando.start();

			terminationDoneSignal.await();
			Diary.getInstance().createOutputFile(args[1]);

		} catch (IOException | InterruptedException e){
			e.printStackTrace();
		}
	}
}
