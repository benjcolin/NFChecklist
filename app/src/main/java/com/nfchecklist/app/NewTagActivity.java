package com.nfchecklist.app;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class NewTagActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.nfchecklist.app.MESSAGE";
    private Tag mytag;
    private Context ctx;
    private EditText tagName;
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_tag);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ctx = this;
        tagName = (EditText) findViewById(R.id.name);
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

    }

    public void writeTag(View view){
        Intent intent = new Intent(this, WriteTagActivity.class);
        intent.putExtra(EXTRA_MESSAGE, tagName.getText().toString());
        startActivity(intent);

        try {
            if(mytag==null){
                Toast.makeText(ctx, ctx.getString(R.string.error_detected), Toast.LENGTH_SHORT ).show();
            }else{
                write(tagName.getText().toString(),mytag);
                Toast.makeText(ctx, ctx.getString(R.string.ok_writing), Toast.LENGTH_SHORT ).show();
            }
        } catch (IOException e) {
            Toast.makeText(ctx, ctx.getString(R.string.error_writing), Toast.LENGTH_SHORT ).show();
            e.printStackTrace();
        } catch (FormatException e) {
            Toast.makeText(ctx, ctx.getString(R.string.error_writing) , Toast.LENGTH_SHORT ).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //Enable forground dispatching to get the tags
        mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null); //ended up setting mFilters and mTechLists to null
    }

    @Override
    protected void onNewIntent(Intent intent){
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
            mytag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Toast.makeText(this, this.getString(R.string.ok_detection) + mytag.toString(), Toast.LENGTH_LONG ).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //You need to disable forgroundDispatching here
        mAdapter.disableForegroundDispatch(this);
    }

    private NdefRecord createRecord(String text) throws UnsupportedEncodingException {

        //create the message in according with the standard
        String lang = "en";
        byte[] textBytes = text.getBytes();
        byte[] langBytes = lang.getBytes("US-ASCII");
        int langLength = langBytes.length;
        int textLength = textBytes.length;

        byte[] payload = new byte[1 + langLength + textLength];
        payload[0] = (byte) langLength;

        // copy langbytes and textbytes into payload
        System.arraycopy(langBytes, 0, payload, 1, langLength);
        System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

        NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload);
        return recordNFC;
    }

    private void write(String text, Tag tag) throws IOException, FormatException {

        NdefRecord[] records = { createRecord(text) };
        NdefMessage message = new NdefMessage(records);
        Ndef ndef = Ndef.get(tag);
        ndef.connect();
        ndef.writeNdefMessage(message);
        ndef.close();
    }

}
