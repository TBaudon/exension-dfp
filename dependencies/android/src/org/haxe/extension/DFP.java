package org.haxe.extension;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest.Builder;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.android.gms.ads.InterstitialAd;

import org.haxe.lime.HaxeObject;

/* 
	You can use the Android Extension class in order to hook
	into the Android activity lifecycle. This is not required
	for standard Java code, this is designed for when you need
	deeper integration.
	
	You can access additional references from the Extension class,
	depending on your needs:
	
	- Extension.assetManager (android.content.res.AssetManager)
	- Extension.callbackHandler (android.os.Handler)
	- Extension.mainActivity (android.app.Activity)
	- Extension.mainContext (android.content.Context)
	- Extension.mainView (android.view.View)
	
	You can also make references to static or instance methods
	and properties on Java classes. These classes can be included 
	as single files using <java path="to/File.java" /> within your
	project, or use the full Android Library Project format (such
	as this example) in order to include your own AndroidManifest
	data, additional dependencies, etc.
	
	These are also optional, though this example shows a static
	function for performing a single task, like returning a value
	back to Haxe from Java.
*/
public class DFP extends Extension {
	
	
	////////////////////////////////////////////////////////////////////////
	static RelativeLayout adLayout;
	static RelativeLayout.LayoutParams adMobLayoutParams, adLayoutParam;
	static PublisherAdView adView;
	static Boolean adVisible = false, adInitialized = false, adTestMode = false;
	static PublisherInterstitialAd interstitial;
	static String deviceHash;
	static Boolean adLayoutAdded = false;
	// listeners
	static HaxeObject mIntersticialListener;
	static String mInterstitialLoaded, mInterstitialError, mInterstitialClosed;
	////////////////////////////////////////////////////////////////////////	
	
	/**
	 * Called when an activity you launched exits, giving you the requestCode 
	 * you started it with, the resultCode it returned, and any additional data 
	 * from it.
	 */
	public boolean onActivityResult (int requestCode, int resultCode, Intent data) {
		return true;
	}
	
	public DFP(){
		super();
		adLayout = new RelativeLayout(mainActivity);
		adMobLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT); 
		mainActivity.addContentView(adLayout, adMobLayoutParams);
		adLayoutAdded = true;
	}
	
	
	/**
	 * Called when the activity is starting.
	 */
	public void onCreate (Bundle savedInstanceState) {
	}
	
	
	/**
	 * Perform any final cleanup before an activity is destroyed.
	 */
	public void onDestroy () {
		
		
		
	}
	
	
	/**
	 * Called as part of the activity lifecycle when an activity is going into
	 * the background, but has not (yet) been killed.
	 */
	public void onPause () {
		if (adView != null) {
			adView.pause();
		}		
		
		if(adLayoutAdded){
			adLayoutAdded  = false;
			ViewGroup vg = (ViewGroup)(adLayout.getParent());
			vg.removeView(adLayout);
		}
	}
	
	
	/**
	 * Called after {@link #onStop} when the current activity is being 
	 * re-displayed to the user (the user has navigated back to it).
	 */
	public void onRestart () {

	}
	
	
	/**
	 * Called after {@link #onRestart}, or {@link #onPause}, for your activity 
	 * to start interacting with the user.
	 */
	public void onResume () {
		if(!adLayoutAdded){
			mainActivity.addContentView(adLayout, adMobLayoutParams);
			adLayoutAdded = true;
		}
		
		if (adView != null) {
			adView.resume();
		}	
	}
	
	
	/**
	 * Called after {@link #onCreate} &mdash; or after {@link #onRestart} when  
	 * the activity had been stopped, but is now again being displayed to the 
	 * user.
	 */
	public void onStart () {
		
	}
	
	
	/**
	 * Called when the activity is no longer visible to the user, because 
	 * another activity has been resumed and is covering this one. 
	 */
	public void onStop () {
	}
	
	public void onSaveInstanceState (Bundle outState) {

	}

	/**
	 * Called after onStart() when the activity is being re-initialized from 
	 * a previously saved state.
	 */
	public void onRestoreInstanceState (Bundle savedState) {

	}
	
	////////////////////////////////////////////////////////////////////////
	static public void loadAd() {
		try {
			Builder adRequestBuilder = new PublisherAdRequest.Builder();
			//AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
			
			if(adTestMode){	
				adRequestBuilder.addTestDevice(PublisherAdRequest.DEVICE_ID_EMULATOR);
				adRequestBuilder.addTestDevice(deviceHash);
			}
			
			PublisherAdRequest adRequest = adRequestBuilder.build();
			//Log.i("trace", "IS TEST : " + adRequest.isTestDevice(mainContext));
		
			adView.loadAd(adRequest);
		}catch(Exception e){
			Log.i("trace","Error loadAd: " + e.getMessage());
		}
	}
	
	static public void initAd(final String id, final int x, final int y, final int size, final boolean testMode) {
		mainActivity.runOnUiThread(new Runnable() {
			public void run() {
				
				adTestMode = testMode;
				
				if (mainActivity == null) {
					return;
				}

				AdSize adsize;
				
				switch(size){
				case 0 : 
					adsize = AdSize.BANNER;
					break;
				case 1 :
					adsize = AdSize.FULL_BANNER;
					break;
				case 2 :
					adsize = AdSize.LARGE_BANNER;
					break;
				case 3 : 
					adsize = AdSize.LEADERBOARD;
					break;
				case 4 :
					adsize = AdSize.MEDIUM_RECTANGLE;
					break;
				case 5 :
					adsize = AdSize.SMART_BANNER;
					break;
				case 6 :
					adsize = AdSize.WIDE_SKYSCRAPER;
					break;
				default :
					adsize = AdSize.SMART_BANNER;
					break;
				}
				
				
				adView = new PublisherAdView(mainActivity);
				adView.setAdUnitId(id);
				adView.setAdSizes(adsize);
				loadAd();

				adLayoutParam = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); 
       
                if(x == 0) 
                	adLayoutParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				else if(x == 1) 
					adLayoutParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				else if(x == 2) 
					adLayoutParam.addRule(RelativeLayout.CENTER_HORIZONTAL);
				
				if(y == 0) 
					adLayoutParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				else if(y == 1) 
					adLayoutParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				else if(y == 2) 
					adLayoutParam.addRule(RelativeLayout.CENTER_VERTICAL);
				
				adInitialized = true;
			}
		});
	}
	
	static public void showAd() {
		mainActivity.runOnUiThread(new Runnable() {
			public void run() {
				try {
					if (adInitialized && !adVisible) {
						adLayout.removeAllViews();
						adView.setBackgroundColor(Color.BLACK);
						adLayout.addView(adView, adLayoutParam);
						adView.setBackgroundColor(0);
						adVisible = true;
					}
				} catch (Exception e){
					Log.i("trace","Error showAd: " + e.getMessage());
				}
			}
		});
	}
        
	static public void hideAd() {
		mainActivity.runOnUiThread(new Runnable() {
			public void run() {
				if (adInitialized && adVisible) {
					adLayout.removeAllViews();
					loadAd();
					adVisible = false;
				}
			}
		});
	}
	
	static public void loadInterstitial() {
		Builder adRequestBuilder = new PublisherAdRequest.Builder();
		if(adTestMode){	
			adRequestBuilder.addTestDevice(PublisherAdRequest.DEVICE_ID_EMULATOR);
			adRequestBuilder.addTestDevice(deviceHash);
		}
		PublisherAdRequest adRequest = adRequestBuilder.build();
		
		interstitial.loadAd(adRequest);
	}
	
	static public void setInterstitialListeners(HaxeObject object, String onAdLoaded, String onAdFailed, String onAdClosed){
		mIntersticialListener = object;
		mInterstitialLoaded = onAdLoaded;
		mInterstitialError = onAdFailed;
		mInterstitialClosed = onAdClosed;
	}
	
	static public void initInterstitial(final String id, final boolean testMode) {
        mainActivity.runOnUiThread(new Runnable() {
            public void run() {
            	adTestMode = testMode;
				if (Extension.mainActivity == null) {
					return;
				}
				
                interstitial = new PublisherInterstitialAd(mainActivity);
                interstitial.setAdUnitId(id);
                interstitial.setAdListener(new AdListener() {
                	
                	public void onAdLoaded(){
                		if(mIntersticialListener != null && mInterstitialLoaded != null)
                			mIntersticialListener.call0(mInterstitialLoaded);
                	}
                	
                	public void onAdFailedToLoad(int errorCode){
                		if(mIntersticialListener != null && mInterstitialError != null)
                			mIntersticialListener.call1(mInterstitialError, errorCode);
                	}
                	
                	public void onAdClosed(){
                		if(mIntersticialListener != null && mInterstitialClosed != null)
                			mIntersticialListener.call0(mInterstitialClosed);
                	}
                	
				});

                loadInterstitial();
            }
        });
    }

    static public void showInterstitial() {
        mainActivity.runOnUiThread(new Runnable() {
            public void run() {
                if (interstitial.isLoaded()) {
                    interstitial.show();
                }
            }
        });
    }
    
    static public void onIntersticialLoaded(){
    	
    }
    
    static public void onIntersticialLoadError(){
    	
    }
    
    static public void onIntersticialClosed(){
    	
    }
    
    static public void setTestDevice(String hash) {
    	deviceHash = hash;
    }
	///////////////////////////////////////////////////////////////////////////////////////////	
}