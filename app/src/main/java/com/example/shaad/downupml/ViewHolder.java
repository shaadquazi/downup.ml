package com.example.shaad.downupml;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


class ViewHolder extends RecyclerView.ViewHolder {
    TextView fname, ftype, fsize;
    Button delete, download;


    public ViewHolder(View itemView) {
        super(itemView);
        fname = (TextView) itemView.findViewById(R.id.fname);
        ftype = (TextView) itemView.findViewById(R.id.ftype);
        fsize = (TextView) itemView.findViewById(R.id.fsize);
        delete = (Button) itemView.findViewById(R.id.delete);
        download = (Button) itemView.findViewById(R.id.download);


    }
}