/*****************************************************************************
 * VLCInstance.java
 *****************************************************************************
 * Copyright © 2011-2014 VLC authors and VideoLAN
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 *****************************************************************************/

package org.videolan.vlc.util;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.LibVlcException;
import org.videolan.libvlc.LibVlcUtil;
import org.videolan.vlc.BuildConfig;
import org.videolan.vlc.VLCApplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

public class VLCInstance {
    public final static String TAG = "VLC/Util/VLCInstance";
    private static LibVLC sLibVLC = null;

    /** A set of utility functions for the VLC application */
    public synchronized static LibVLC get() throws IllegalStateException {
        if (sLibVLC == null) {
            //Thread.setDefaultUncaughtExceptionHandler(new VLCCrashHandler());

            sLibVLC = new LibVLC();
            final Context context = VLCApplication.getAppContext();
            //SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            VLCInstance.updateLibVlcSettings();
            try {
                sLibVLC.init(context);
            } catch (LibVlcException e) {
                throw new IllegalStateException("LibVLC initialisation failed: " + LibVlcUtil.getErrorMsg());
            }
            //FIXME crash set
           /* LibVLC.setOnNativeCrashListener(new LibVLC.OnNativeCrashListener() {
                @Override
                public void onNativeCrash() {
                    Intent i = new Intent(context, NativeCrashActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("PID", android.os.Process.myPid());
                    context.startActivity(i);
                }
            });*/
        }
        return sLibVLC;
    }

    public static synchronized void restart(Context context) throws IllegalStateException {
        if (sLibVLC != null) {
            try {
                sLibVLC.destroy();
                sLibVLC.init(context);
            } catch (LibVlcException lve) {
                throw new IllegalStateException("LibVLC initialisation failed: " + LibVlcUtil.getErrorMsg());
            }
        }
    }

    public static synchronized boolean testCompatibleCPU(Context context) {
        if (sLibVLC == null && !LibVlcUtil.hasCompatibleCPU(context)) {
           /* final Intent i = new Intent(context, CompatErrorActivity.class);
            context.startActivity(i);*/
            return false;
        } else
            return true;
    }

    public static synchronized void updateLibVlcSettings() {
        if (sLibVLC == null)
            return;

        sLibVLC.setSubtitlesEncoding("");
        sLibVLC.setTimeStretching(false);
        sLibVLC.setFrameSkip(false);
        
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD){
        	sLibVLC.setChroma("YV12");
        }else{
        	sLibVLC.setChroma("RV16");
        }
        sLibVLC.setVerboseMode(BuildConfig.DEBUG);

        /*if (pref.getBoolean("equalizer_enabled", false))
            sLibVLC.setEqualizer(Preferences.getFloatArray(pref, "equalizer_values"));*/

        int aout=-1;
        //0 suface,1 opengles2
        int vout=0;
        int deblocking=-1;
       /* public static final int HW_ACCELERATION_AUTOMATIC = -1;
        public static final int HW_ACCELERATION_DISABLED = 0;
        public static final int HW_ACCELERATION_DECODING = 1;
        public static final int HW_ACCELERATION_FULL = 2;*/
        int hardwareAcceleration=LibVLC.HW_ACCELERATION_AUTOMATIC;
       /* public static final int DEV_HW_DECODER_AUTOMATIC = -1;
        public static final int DEV_HW_DECODER_OMX = 0;
        public static final int DEV_HW_DECODER_OMX_DR = 1;
        public static final int DEV_HW_DECODER_MEDIACODEC = 2;
        public static final int DEV_HW_DECODER_MEDIACODEC_DR = 3;*/
        int devHardwareDecoder=LibVLC.DEV_HW_DECODER_AUTOMATIC;
        int networkCaching = 0;
        sLibVLC.setAout(aout);
        sLibVLC.setVout(vout);
        sLibVLC.setDeblocking(deblocking);
        sLibVLC.setNetworkCaching(networkCaching);
        sLibVLC.setHardwareAcceleration(hardwareAcceleration);
        sLibVLC.setDevHardwareDecoder(devHardwareDecoder);
        sLibVLC.setFrameSkip(false);

    }

    public static synchronized void setAudioHdmiEnabled(Context context, boolean enabled) {
        if (sLibVLC != null && sLibVLC.isHdmiAudioEnabled() != enabled) {
            sLibVLC.setHdmiAudioEnabled(enabled);
            restart(context);
        }
    }
}
