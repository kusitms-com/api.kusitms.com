package com.kusitms.website.domain.chat.repository;

public interface UnreadCountProjection {

    Long getChatRoomId();

    Long getUnreadCount();
}
