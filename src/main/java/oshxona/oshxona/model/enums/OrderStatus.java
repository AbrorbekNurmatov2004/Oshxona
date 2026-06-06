package oshxona.oshxona.model.enums;

public enum OrderStatus {
    CREATED,
    CANCELED,
    ACCEPTED,
    DONE;

    public static String statusText(OrderStatus status) {
        return switch (status) {
            case CANCELED -> "Buyurtma bekor qilingan❌";
            case CREATED -> "Jarayonda...🕑";
            case ACCEPTED -> "Buyurtma tayorlanmoqda...🧑‍🍳";
            case DONE -> "Buyurtma manzilga yetqazildi🚗";
        };
    }
}


