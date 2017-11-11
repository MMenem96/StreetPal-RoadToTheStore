package com.sharekeg.streetpal.chatcomponents;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.sharekeg.streetpal.R;

import java.util.List;

import static java.security.AccessController.getContext;

/**
 * Created by MMenem on 8/2/2017.
 */


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.chatHolder> {
    List<ChatMessage> ChatMessage;
    Context context;
    private View v;
    private OnUserStatusChangeListener listener;
    private int lastPosition = -1;

    public ChatAdapter(Context context, List<ChatMessage> ChatMessage, OnUserStatusChangeListener listener) {
        this.ChatMessage = ChatMessage;
        this.context = context;
        this.listener = listener;
    }


    @Override
    public chatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_message_rec, parent, false);
        return new chatHolder(v);
    }

    @Override
    public void onBindViewHolder(final chatHolder holder, final int position) {
        Log.i("Chat_Message", ChatMessage.get(position).toString());
        if (ChatMessage.get(position).isUserMessage()) {
            holder.streetPalLogo.setVisibility(View.GONE);
            holder.tvMessageRec.setVisibility(View.GONE);
//            holder.userChatLogo.setVisibility(View.VISIBLE);
//            holder.tvMessageRec.setGravity(R elativeLayout.ALIGN_PARENT_RIGHT);
            holder.userMessage.setText(ChatMessage.get(position).getMessageText());
            holder.userMessage.setVisibility(View.VISIBLE);
           setAnimation(holder.itemView, position);
        } else {
            holder.userMessage.setVisibility(View.GONE);
            holder.tvMessageRec.setText(Html.fromHtml(ChatMessage.get(position).getMessageText()));
            holder.streetPalLogo.setVisibility(View.VISIBLE);
            holder.tvMessageRec.setVisibility(View.VISIBLE);
           setAnimation(holder.itemView, position);

        }
        holder.tvMessageRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ChatMessage.get(holder.getAdapterPosition()).isHasNavigationLink()) {
                    if (ChatMessage.get(holder.getAdapterPosition()).getNavigationTag().equals(UserGuide.CALL_TRUSTED_CONTACT)) {
                        listener.OnUserStatusChange(UserGuide.SEND_STRESS_SIGNAL);
                    } else {
                        listener.OnUserNavigation(ChatMessage.get(holder.getAdapterPosition()).getNavigationTag());
                    }

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return ChatMessage.size();
    }

    public class chatHolder extends RecyclerView.ViewHolder {

        TextView tvMessageRec, userMessage;
        ImageView streetPalLogo, userChatLogo;

        public chatHolder(View itemView) {
            super(itemView);
            tvMessageRec = (TextView) itemView.findViewById(R.id.tvmessage_send);
            streetPalLogo = (ImageView) itemView.findViewById(R.id.logo_street_pal);
            userMessage = (TextView) itemView.findViewById(R.id.tvmessage_user_text);
            //  userChatLogo = (ImageView) itemView.findViewById(R.id.logo_chat_user);

        }
    }




    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;











        }
    }
}
