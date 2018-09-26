package red.softgrip.com.dowloadvideos;

/**
 * Created by Qaiser Pasha on 9/24/2018.
 */

import android.Manifest;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class DownloadTab  extends Fragment implements EasyPermissions.PermissionCallbacks {



    private static final int WRITE_REQUEST_CODE = 300;
    private static final String TAG = MainActivity.class.getSimpleName();
    private String url;
    private EditText editTextUrl;
    ProgressDialog progressDialog;
    String abc;
    Elements metaTags;

    String absoluteUrl,srcValue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_down, container, false);

        editTextUrl = rootview.findViewById(R.id.et_URL);
        Button downloadButton = rootview.findViewById(R.id.btn_down);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Fetching");
        progressDialog.setMessage("Please wait ....");

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check if SD card is present or not
                if (CheckForSDCard.isSDCardPresent()) {

                    //check if app has permission to write to the external storage.
                    if (EasyPermissions.hasPermissions(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        //Get the URL entered

                        Content content=new Content();
                                content.execute();






                        progressDialog.show();


                        new Handler().postDelayed(new Runnable() {


                            @Override
                            public void run() {

                                Content content=new Content();
                                content.execute();

                            }
                        }, 2000);
//                        url = editTextUrl.getText().toString();
//                        new DownloadFile().execute(url);

                    } else {
                        //If permission is not present request for the same.
                        EasyPermissions.requestPermissions(getActivity(), getString(R.string.write_file), WRITE_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
                    }


                } else {
                    Toast.makeText(getContext(),
                            "SD Card not found", Toast.LENGTH_LONG).show();

                }
            }

        });

        return rootview;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, getActivity());
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        //Download the file once permission is granted
        url = editTextUrl.getText().toString();
        new DownloadFile().execute(url);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG, "Permission has been denied");
    }

    /**
     * Async Task to download file from URL
     */
    private class DownloadFile extends AsyncTask<String, String, String> {

        private ProgressDialog progressDialog;
        private String fileName;
        private String folder;
        private boolean isDownloaded;

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.progressDialog = new ProgressDialog(getActivity());
            this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {


                //  System.out.println(descriptionMetaTag);

                // Get content of the above description meta tag


                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                // getting file length
                int lengthOfFile = connection.getContentLength();


                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

                //Extract file name from URL
                fileName = f_url[0].substring(f_url[0].lastIndexOf('/') + 1, f_url[0].length());

                //Append timestamp to file name
                fileName = timestamp + "_" + fileName+".mp4";

                //External directory path to save file
                folder = Environment.getExternalStorageDirectory() + File.separator +"MusicallyDownloader/";

                //Create androiddeft folder if it does not exist
                File directory = new File(folder);

                if (!directory.exists()) {
                    directory.mkdirs();
                }

                // Output stream to write file
                OutputStream output = new FileOutputStream(folder + fileName);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));
                    Log.d(TAG, "Progress: " + (int) ((total * 100) / lengthOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();
                return "Downloaded at: " + folder + fileName;

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return "Something went wrong";
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            progressDialog.setProgress(Integer.parseInt(progress[0]));


        }


        @Override
        protected void onPostExecute(String message) {
            // dismiss the dialog after the file was downloaded
            this.progressDialog.dismiss();


            // Display File path after downloading
            Toast.makeText(getContext(),
                    message, Toast.LENGTH_LONG).show();
        }
    }




    private class Content extends AsyncTask<Void, Void, Void>
    {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progressDialog=new ProgressDialog(MainActivity.this);
//            progressDialog.show();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // textView.setText(metaTags.text());

//            progressDialog.dismiss();
            Toast.makeText(getActivity(), "value :"+absoluteUrl, Toast.LENGTH_SHORT).show();
            Toast.makeText(getActivity(), "result :"+srcValue, Toast.LENGTH_SHORT).show();
             //   String finalvalue="https:"+abc;
//                                    url = editTextUrl.getText().toString();
                    //    new DownloadFile().execute(finalvalue);

//                        progressDialog.dismiss();


        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(Void... voids) {





            try {





                Document document = Jsoup.connect("http://vm.tiktok.com/KVyHr/").get();

                Element div1 = document.getElementById("jp_video_0");
                System.out.println("\nlinkwww: " + div1.attr("src"));




//                title=document.title();
//                Element link = document.select("video").first();
//
//                String text = document.body().text(); // "An example link"
//                String linkHref = link.attr("src"); // "http://example.com/"
//                String linkText = link.text();
//                System.out.println("abc : " +linkText);
//                Element imageElement = document.select("img").first();
//
//                 absoluteUrl = imageElement.absUrl("src");  //absolute URL on src
//
//                 srcValue = imageElement.attr("src");
//                System.out.println("content : " +absoluteUrl);


//                metaTags = document.select("meta");
//                //  System.out.println(metaTags);
//                Element descriptionMetaTag = document.select("meta[property=og:video:url]").first();
//
//                //  System.out.println(descriptionMetaTag);
//
//                // Get content of the above description meta tag
//                abc=descriptionMetaTag.attr("content");
//                System.out.println("content : " + descriptionMetaTag.attr("content"));

            } catch (IOException e) {
                e.printStackTrace();
            }




            return null;
        }



    }

}
