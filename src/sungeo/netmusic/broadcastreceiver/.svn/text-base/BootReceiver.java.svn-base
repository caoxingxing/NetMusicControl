package sungeo.netmusic.broadcastreceiver;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver{
	@Override
	public void onReceive(Context ctx, Intent intent) {
		/*Intent myi=new Intent(ctx, MainActivity.class);
		myi.setAction("android.intent.action.MAIN");
		myi.addCategory("android.intent.category.LAUNCHER");
		myi.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ctx.startActivity(myi);*/

		KeyguardManager keyguardManager = (KeyguardManager)ctx.getSystemService(Context.KEYGUARD_SERVICE);
		KeyguardLock keyguradlock = keyguardManager.newKeyguardLock("");
		//keyguradlock.reenableKeyguard();
		keyguradlock.disableKeyguard();
	}
}
