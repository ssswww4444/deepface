package au.edu.unimelb.eresearch.happypeople;

import android.app.Application;
import android.util.DisplayMetrics;


public class App extends Application {

    protected static App       mInstance;
    private DisplayMetrics     displayMetrics = null;

    public App(){
        mInstance = this;
    }

    public static App getApp() {
        if (mInstance != null && mInstance instanceof App) {
            return (App) mInstance;
        } else {
            mInstance = new App();
            mInstance.onCreate();
            return (App) mInstance;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public float getScreenDensity() {
        if (this.displayMetrics == null) {
            setDisplayMetrics(getResources().getDisplayMetrics());
        }
        return this.displayMetrics.density;
    }

    public int getScreenHeight() {
        if (this.displayMetrics == null) {
            setDisplayMetrics(getResources().getDisplayMetrics());
        }
        return this.displayMetrics.heightPixels;
    }

    public int getScreenWidth() {
        if (this.displayMetrics == null) {
            setDisplayMetrics(getResources().getDisplayMetrics());
        }
        return this.displayMetrics.widthPixels;
    }

    public void setDisplayMetrics(DisplayMetrics DisplayMetrics) {
        this.displayMetrics = DisplayMetrics;
    }

    public int dp2px(float f)
    {
        return (int)(0.5F + f * getScreenDensity());
    }

    public int px2dp(float pxValue) {
        return (int) (pxValue / getScreenDensity() + 0.5f);
    }


    /**
     * Used to get the 'data/data/....File'  path
     * @return the path string
     */
    public String getFilesDirPath() {
        return getFilesDir().getAbsolutePath();
    }


    /**
     * Used to get the 'data/data/....Cache'  path
     * @return the path sting
     */
    public String getCacheDirPath() {
        return getCacheDir().getAbsolutePath();
    }

}
