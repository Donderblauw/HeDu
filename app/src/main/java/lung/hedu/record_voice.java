package lung.hedu;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.LinearLayout;

/**
 * Created by groentje2015 on 10/15/2015.
 */
public class record_voice {

    static public class time_counter extends AsyncTask<Void, Void, Integer[]> {

        protected Integer[] doInBackground(Void...voids) {
            Log.e("result", "in loop");
            int tel = 0;
            // http://www.anddev.org/novice-tutorials-f8/get-frequency-data-from-microphone-in-real-time-t16774-15.html

            AudioRecord recorder;
            int numCrossing, p;
            short audioData[];
            int bufferSize;
            int total = 150;
            Integer tel_freq_peak[][] = new Integer[total][2];
            int numert = 8000;

            bufferSize = AudioRecord.getMinBufferSize(numert, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT) ; //get the buffer size to use with this audio record

            recorder = new AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION, numert, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize); //instantiate the AudioRecorder

            audioData = new short[bufferSize]; //short array that pcm data is put into.
            short audioData2[][] = new short[total][bufferSize]; //short array that pcm data is put into.
//							recorder.
            recorder.startRecording();

            if (recorder.getState() == android.media.AudioRecord.STATE_INITIALIZED) // check to see if the recorder has initialized yet.
                if (recorder.getRecordingState() == android.media.AudioRecord.RECORDSTATE_STOPPED)
                    recorder.startRecording();  //check to see if the Recorder has stopped or is not recording, and make it record.

            boolean loze_first12 = false;


            tel = 0;
            while (tel < total) {  //loop while recording is needed
                {
                    recorder.read(audioData, 0, (bufferSize/4));
                    if(loze_first12 == false)
                    {
                        if(tel == 12)
                        {
                            loze_first12 = true;
                            tel = 0;
                        }
                    }
                    else {

                        loze_first12 = true;
                        for (p = 0; p < (bufferSize / 4); p += 1) {
                            audioData2[tel][p] = audioData[p];

                        }
//                    publishProgress("Recording: " + tel);
                    }
                    tel = tel + 1;
                }//else recorder started
            } //while recording
            Log.e("result", "done");
            tel = 0;
            int frequency = 0;

            while (tel < total) {
                {
                    numCrossing = 0;
                    int numSamples = 0;
                    int peaks = 0;
                    // int mod = (int) (bufferSize / 4) * 4;

                    for (p = 0; p < bufferSize; p++) {
                        peaks = peaks + java.lang.Math.abs(audioData2[tel][p]); //add together highes and lows from each sample
                        if (audioData2[tel][p] > 0 && audioData2[tel][p + 1] <= 0) numCrossing++;
                        if (audioData2[tel][p] < 0 && audioData2[tel][p + 1] >= 0) numCrossing++;
                        numSamples++;
                    }
                    peaks = peaks / numSamples;
                    frequency = (numert / numSamples) * (numCrossing / 2);  // Set the audio Frequency to half the number of zero crossings, times the number of samples our buffersize is per second.
                    tel_freq_peak[tel][0] = frequency;
                    tel_freq_peak[tel][1] = (peaks * frequency);
                    tel = tel + 1;
                }
            }

//        publishProgress("Stoped recording, proccessing");

            if (recorder.getState() == android.media.AudioRecord.RECORDSTATE_RECORDING)
                recorder.stop(); //stop the recorder before ending the thread
            recorder.release(); //release the recorders resources
            recorder = null; //set the recorder to be garbage collected.

            tel = 0;
            int avg_feq = 0;
            int close_to_stdev_feq = 0;
            int highest_feq = 0;
            int avg_peak = 0;
            int lowest_peak = 0;
            int highest_peak = 0;
            int lowest_peak_number = 0;
            int highest_peak_number = 0;
            int close_to_stdev_peak = 0;
            int treshold = 0;
            int treshold_test = 0;
            while (tel < total) {
                avg_feq = avg_feq + tel_freq_peak[tel][0];
                avg_peak = avg_peak + tel_freq_peak[tel][1];

                if (lowest_peak > tel_freq_peak[tel][1]) {
                    if (tel_freq_peak[tel][1] != 0) {
                        lowest_peak = tel_freq_peak[tel][1];
                        lowest_peak_number = tel;
                    }
                }
                if (highest_peak < tel_freq_peak[tel][1]) {
                    highest_peak = tel_freq_peak[tel][1];
                    highest_peak_number = tel;
                }
                if (highest_feq < tel_freq_peak[tel][0]) {
                    highest_feq = tel_freq_peak[tel][0];
                }

                if (tel > 3) {
                    int simple_peak_check = tel_freq_peak[tel][1] - tel_freq_peak[(tel - 1)][1];
                    // better noise detection ? it's noise if the lowest peak *7 ? verschil met hoogste ?
                    treshold = lowest_peak * 7;
                    treshold_test = highest_peak - (((highest_peak - lowest_peak) / 7) * 6);
                    if (treshold < treshold_test) {
                        treshold = treshold_test;
                    }
                    if (simple_peak_check < treshold) {
                        close_to_stdev_peak = close_to_stdev_peak + Math.abs((int) (tel_freq_peak[tel][1] - (double) ((double) avg_peak / tel)));
                        close_to_stdev_feq = close_to_stdev_feq + Math.abs((int) (tel_freq_peak[tel][0] - (double) ((double) avg_feq / tel)));
                    }
                }

                tel = tel + 1;
            }
            avg_feq = (avg_feq / total);
            avg_peak = (avg_peak / total);

            int noise_limit_peak = (int) (lowest_peak + (double) ((double) close_to_stdev_peak / total))*2;
            int noise_limit_freq = (int) (double) ((double) close_to_stdev_feq / total);

            boolean stop = false;

            boolean new_fragment = false;
            int delta_tot_freq = 0;
            int delta_tot_peak = 0;

            int higest_freq_prec = 0;
            int lowest_freq_prec = 0;
            int higest_freq_pos = 0;
            int lowest_freq_pos = 0;
            int higest_peak_prec = 0;
            int lowest_peak_prec = 0;
            int higest_peak_pos = 0;
            int lowest_peak_pos = 0;

            int freq_avg_prec = 0;
            int amound_of_points = 0;
            Boolean second_below_treshold = false;

            Integer return_values[] = new Integer[12];

            tel = 0;
            int delta_up = 0;
           String to_text = "avg_feq=" + avg_feq + " lim=" + noise_limit_freq + " std=" + close_to_stdev_feq + " ---- avg_peak=" + avg_peak + " lim=" + noise_limit_peak + " \n";
            Log.e("result", to_text);
            while (tel < total) {
                Log.e("result", ""+tel_freq_peak[tel][1]);
                if (tel_freq_peak[tel][1] > noise_limit_peak && stop == false) {
                    second_below_treshold = false;

                    if (new_fragment == false)
                    {
                        new_fragment = true;
                        amound_of_points = 1;
                        freq_avg_prec = tel_freq_peak[tel][0];
                        higest_freq_prec = tel_freq_peak[tel][0];
                        lowest_freq_prec = tel_freq_peak[tel][0];
                        higest_peak_prec = tel_freq_peak[tel][1];
                        lowest_peak_prec = tel_freq_peak[tel][1];
                    }
                    else
                    {
                        delta_tot_freq = delta_tot_freq + Math.abs(tel_freq_peak[tel][0] - tel_freq_peak[(tel - 1)][0]);
                        delta_tot_peak = delta_tot_peak + Math.abs(tel_freq_peak[tel][1] - tel_freq_peak[(tel - 1)][1]);
                        delta_up = (tel_freq_peak[tel][0] - tel_freq_peak[(tel - 1)][0]) * amound_of_points;

                        if (higest_freq_prec < tel_freq_peak[tel][0]) {
                            higest_freq_prec = tel_freq_peak[tel][0];
                            higest_freq_pos = amound_of_points;
                        }
                        if (lowest_freq_prec > tel_freq_peak[tel][0]) {
                            lowest_freq_prec = tel_freq_peak[tel][0];
                            lowest_freq_pos = amound_of_points;
                        }
                        if (higest_peak_prec > tel_freq_peak[tel][1]) {
                            higest_peak_prec = tel_freq_peak[tel][1];
                            higest_peak_pos = amound_of_points;
                        }
                        if (lowest_peak_prec < tel_freq_peak[tel][1]) {
                            lowest_peak_prec = tel_freq_peak[tel][1];
                            lowest_peak_pos = amound_of_points;
                        }

                        amound_of_points = amound_of_points + 1;
                        freq_avg_prec = freq_avg_prec + tel_freq_peak[tel][0];

                    }

                }
                else
                {
                    if(second_below_treshold == false)
                    {
                        second_below_treshold = true;
                    }
                    else {

                        new_fragment = false;
                        if (amound_of_points > 2) {
                            stop = true;
                        }
                    }
                }

                tel = tel + 1;
            }


            if (stop == true) {
                return_values[0] = delta_tot_freq / amound_of_points;
                return_values[1] = delta_tot_peak / amound_of_points;
                return_values[2] = higest_freq_prec;
                return_values[3] = lowest_freq_prec;
                return_values[4] = (delta_tot_freq - delta_up) / amound_of_points;
                return_values[5] = delta_up / amound_of_points;
                return_values[6] = freq_avg_prec / amound_of_points;
                return_values[7] = amound_of_points;
                return_values[8] = higest_freq_pos;
                return_values[9] = lowest_freq_pos;
                return_values[10] = higest_peak_pos;
                return_values[11] = lowest_peak_pos;
            } else {
                return_values[0] = 0;
                return_values[1] = 0;
                return_values[2] = 0;
                return_values[3] = 0;
                return_values[4] = 0;
                return_values[5] = 0;
                return_values[6] = 0;
                return_values[7] = 0;
            }
            // publishProgress("Done");

            return return_values;
        }

        // public Questionnaire.AsyncResponse delegate=null;

        @Override
        protected void onPostExecute(Integer[] result) {

 //           delegate.processFinish(result);

            // showDialog("Downloaded " + result + " bytes");
            int tel = 0 ;
            while (tel < 12)
            {
                Log.e("result", tel + " =" + result[tel]);
                tel = tel +1;
            }

  //          Questionnaire.found_spoken_awnser(result);
        }


    }

    static public Integer[] record_sound ()
    {

            int tel = 0;
            // http://www.anddev.org/novice-tutorials-f8/get-frequency-data-from-microphone-in-real-time-t16774-15.html

            AudioRecord recorder;
            int numCrossing, p;
            short audioData[];
            int bufferSize;
            int total_sound_frag = 100;
            int amound_subfrags = 24;
            int total_classes = total_sound_frag * amound_subfrags;
            Integer tel_freq_peak[][] = new Integer[total_classes][2];
            int numert = 8000;

            bufferSize = AudioRecord.getMinBufferSize(numert, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT) *3; //get the buffer size to use with this audio record

            recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, numert, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize); //instantiate the AudioRecorder

            audioData = new short[bufferSize]; //short array that pcm data is put into.
            short audioData2[][] = new short[total_sound_frag][bufferSize]; //short array that pcm data is put into.

            recorder.startRecording();

            if (recorder.getState() == android.media.AudioRecord.STATE_INITIALIZED) // check to see if the recorder has initialized yet.
            if (recorder.getRecordingState() == android.media.AudioRecord.RECORDSTATE_STOPPED)
                recorder.startRecording();  //check to see if the Recorder has stopped or is not recording, and make it record.

            // boolean loze_first12 = false;


            tel = 0;
            while (tel < total_sound_frag) {  //loop while recording is needed
                {
                    recorder.read(audioData2[tel], 0, (bufferSize));
                    /*
                    if(loze_first12 == false)
                    {
                        if(tel == 12)
                        {
                            loze_first12 = true;
                            tel = 0;
                        }
                    }
                    else {

                        loze_first12 = true;

                        for (p = 0; p < (bufferSize); p += 1) {
                            audioData2[tel][p] = audioData[p];


                        }
                        */
                    //}
                    tel = tel + 1;
                }//else recorder started
            } //while recording
            // Log.e("result", "done");
            tel = 0;


            double tot_cycle = java.lang.Math.floor( bufferSize / amound_subfrags)/2;
        // unkwn div 2

            while (tel < total_sound_frag)
            {

                int tel_subfrag = 0;
                while(tel_subfrag < amound_subfrags)
                {
                    int frequency = 0;
                    numCrossing = 0;
                    // int numSamples = 0;
                    int peaks = 0;
//                    boolean peaked_neg = false;
//                    boolean peaked_pos = false;
                    int noise_quick = 0;
                    int tel_added_class = (tel* amound_subfrags )+tel_subfrag;

                    for (p = 0; p < tot_cycle; p++) {

                        int p_atm = p + ((int)tot_cycle * tel_subfrag);


                        //Log.e("results_p", "tel:"+tel+" p:"+p+" audio:"+audioData2[tel][p_atm]+" buffersize:"+bufferSize);
                        // int abs_delta = java.lang.Math.abs(audioData2[tel][p_atm] - audioData2[tel][p_atm - 1]);
/*
                        if (p < 24) {
                            noise_quick = noise_quick + (abs_delta / 24);
                        } else {

                            //if (java.lang.Math.abs(audioData2[tel][p_atm]) > (noise_quick * 3)) {
                                if (java.lang.Math.abs(audioData2[tel][p_atm]) < 0) {
                                    if (peaked_pos) {
                                        peaked_pos = false;
                                        peaked_neg = true;
                                        numCrossing++;
                                    }
                                } else {
                                    if (peaked_neg) {
                                        peaked_pos = true;
                                        peaked_neg = false;
                                        numCrossing++;
                                    }
                                }
                            //}

                        */
                        if (audioData2[tel][p_atm] > 0 && audioData2[tel][p_atm + 1] <= 0)
                            numCrossing++;
                        if (audioData2[tel][p_atm] < 0 && audioData2[tel][p_atm + 1] >= 0)
                            numCrossing++;


                        //}
                        peaks = peaks + java.lang.Math.abs(audioData2[tel][p_atm]); //add together highes and lows from each sample
                        // numSamples++;
                    }
                    peaks = peaks / (int)tot_cycle;
                    frequency = (int) ((numCrossing * tot_cycle) /tot_cycle);  // Set the audio Frequency to half the number of zero crossings, times the number of samples our buffersize is per second.
                    tel_freq_peak[tel_added_class][0] = frequency;
                    tel_freq_peak[tel_added_class][1] = (peaks * frequency);

                    // Log.e("result", tel_subfrag+" peak:"+peaks+" freq:"+frequency+" noise:"+ noise_quick);

                    tel_subfrag++;
                }
                tel = tel + 1;
            }


//        publishProgress("Stoped recording, proccessing");

            if (recorder.getState() == android.media.AudioRecord.RECORDSTATE_RECORDING)
                recorder.stop(); //stop the recorder before ending the thread
            recorder.release(); //release the recorders resources
            recorder = null; //set the recorder to be garbage collected.

            tel = 0;
            int avg_feq = 0;
            int close_to_stdev_feq = 0;
            int highest_feq = 0;
            int avg_peak = 0;
            int lowest_peak = 0;
            int highest_peak = 0;
            int lowest_peak_number = 0;
            int highest_peak_number = 0;
            int close_to_stdev_peak = 0;
            int treshold = 0;
            int treshold_test = 0;
            while (tel < total_classes) {
                avg_feq = avg_feq + tel_freq_peak[tel][0];
                avg_peak = avg_peak + tel_freq_peak[tel][1];

                if (lowest_peak > tel_freq_peak[tel][1]) {
                    if (tel_freq_peak[tel][1] != 0) {
                        lowest_peak = tel_freq_peak[tel][1];
                        lowest_peak_number = tel;
                    }
                }
                if (highest_peak < tel_freq_peak[tel][1]) {
                    highest_peak = tel_freq_peak[tel][1];
                    highest_peak_number = tel;
                }
                if (highest_feq < tel_freq_peak[tel][0]) {
                    highest_feq = tel_freq_peak[tel][0];
                }

                if (tel > 3) {
                    int simple_peak_check = tel_freq_peak[tel][1] - tel_freq_peak[(tel - 1)][1];
                    // better noise detection ? it's noise if the lowest peak *7 ? verschil met hoogste ?
                    treshold = lowest_peak * 7;
                    treshold_test = highest_peak - (((highest_peak - lowest_peak) / 7) * 6);
                    if (treshold < treshold_test) {
                        treshold = treshold_test;
                    }
                    if (simple_peak_check < treshold) {
                        close_to_stdev_peak = close_to_stdev_peak + Math.abs((int) (tel_freq_peak[tel][1] - (double) ((double) avg_peak / tel)));
                        close_to_stdev_feq = close_to_stdev_feq + Math.abs((int) (tel_freq_peak[tel][0] - (double) ((double) avg_feq / tel)));
                    }
                }

                tel = tel + 1;
            }
            avg_feq = (avg_feq / total_classes);
            avg_peak = (avg_peak / total_classes);

            int noise_limit_peak = (int) (lowest_peak + (double) ((double) close_to_stdev_peak / total_classes))*2;
            int noise_limit_freq = (int) (double) ((double) close_to_stdev_feq / total_classes);

            boolean stop = false;

            boolean new_fragment = false;
            int delta_tot_freq = 0;
            int delta_tot_peak = 0;

            int higest_freq_prec = 0;
            int lowest_freq_prec = 0;
            int higest_freq_pos = 0;
            int lowest_freq_pos = 0;
            int higest_peak_prec = 0;
            int lowest_peak_prec = 0;
            int higest_peak_pos = 0;
            int lowest_peak_pos = 0;

            int freq_avg_prec = 0;
            int amound_of_points = 0;
            Boolean second_below_treshold = false;

            Integer return_values[] = new Integer[12];

            tel = 0;
            int delta_up = 0;
             // String to_text = "avg_feq=" + avg_feq + " lim=" + noise_limit_freq + " std=" + close_to_stdev_feq + " ---- avg_peak=" + avg_peak + " lim=" + noise_limit_peak + " \n";
             // Log.e("result", to_text);
            while (tel < total_classes) {

                if (tel_freq_peak[tel][1] > noise_limit_peak && stop == false) {
                    // Log.e("result", ""+tel_freq_peak[tel][1]);
                    second_below_treshold = false;

                    if (new_fragment == false)
                    {
                        new_fragment = true;
                        amound_of_points = 1;
                        freq_avg_prec = tel_freq_peak[tel][0];
                        higest_freq_prec = tel_freq_peak[tel][0];
                        lowest_freq_prec = tel_freq_peak[tel][0];
                        higest_peak_prec = tel_freq_peak[tel][1];
                        lowest_peak_prec = tel_freq_peak[tel][1];
                    }
                    else
                    {
                        delta_tot_freq = delta_tot_freq + Math.abs(tel_freq_peak[tel][0] - tel_freq_peak[(tel - 1)][0]);
                        delta_tot_peak = delta_tot_peak + Math.abs(tel_freq_peak[tel][1] - tel_freq_peak[(tel - 1)][1]);
                        delta_up = (tel_freq_peak[tel][0] - tel_freq_peak[(tel - 1)][0]) * amound_of_points;

                        if (higest_freq_prec < tel_freq_peak[tel][0]) {
                            higest_freq_prec = tel_freq_peak[tel][0];
                            higest_freq_pos = amound_of_points;
                        }
                        if (lowest_freq_prec > tel_freq_peak[tel][0]) {
                            lowest_freq_prec = tel_freq_peak[tel][0];
                            lowest_freq_pos = amound_of_points;
                        }
                        if (higest_peak_prec > tel_freq_peak[tel][1]) {
                            higest_peak_prec = tel_freq_peak[tel][1];
                            higest_peak_pos = amound_of_points;
                        }
                        if (lowest_peak_prec < tel_freq_peak[tel][1]) {
                            lowest_peak_prec = tel_freq_peak[tel][1];
                            lowest_peak_pos = amound_of_points;
                        }

                        amound_of_points = amound_of_points + 1;
                        freq_avg_prec = freq_avg_prec + tel_freq_peak[tel][0];

                    }

                }
                else
                {
                    if(second_below_treshold == false)
                    {
                        second_below_treshold = true;
                    }
                    else {

                        new_fragment = false;
                        if (amound_of_points > 2) {
                            stop = true;
                        }
                    }
                }

                tel = tel + 1;
            }


            if (stop == true) {
                return_values[0] = delta_tot_freq / amound_of_points;
                return_values[1] = delta_tot_peak / amound_of_points;
                return_values[2] = higest_freq_prec;
                return_values[3] = lowest_freq_prec;
                return_values[4] = (delta_tot_freq - delta_up) / amound_of_points;
                return_values[5] = delta_up / amound_of_points;
                return_values[6] = freq_avg_prec / amound_of_points;
                return_values[7] = amound_of_points;
                return_values[8] = higest_freq_pos;
                return_values[9] = lowest_freq_pos;
                return_values[10] = higest_peak_pos;
                return_values[11] = lowest_peak_pos;
            } else {
                return_values[0] = 0;
                return_values[1] = 0;
                return_values[2] = 0;
                return_values[3] = 0;
                return_values[4] = 0;
                return_values[5] = 0;
                return_values[6] = 0;
                return_values[7] = 0;
            }
            // publishProgress("Done");

            return return_values;
        }


    }




