package com.nexturn.ModifiedViews;

public class Proposal {

    public String proposalText, senderUid, senderIMG, recieverUid, senderName, senderPhone, senderDest, senderFB, key, recieverName, recieverImage;

    public Proposal() {
        proposalText = "";
        senderIMG = "";
        senderUid = "";
        recieverUid = "";
        senderName = "";
    }

    public Proposal(Proposal e) {
        proposalText = e.proposalText;
        senderIMG = e.senderIMG;
        senderUid = e.senderUid;
        recieverUid = e.recieverUid;
        senderName = e.senderName;
        key = e.key;
        senderPhone = e.senderPhone;
        senderDest = e.senderDest;
        senderFB = e.senderFB;
        recieverName = e.recieverName;
        recieverImage = e.recieverImage;

    }
}
