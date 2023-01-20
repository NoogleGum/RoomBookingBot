public class Room {
    private String blockName;
    String roomName;
    boolean[][] bookingField;
    public Room(String roomName){
        this.roomName = roomName;
        bookingField = new boolean[5][9];
    }
    void reset(){
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 9; j++) {
                bookingField[i][j] = false;
            }
        }
    }
    public String getRoomName(){
        return roomName;
    }

    @Override
    public String toString(){
        return blockName + " " + roomName;
    }

    public boolean isBooked(int dayOfWeek, String timeSlot){
        String str1 = timeSlot.substring(0,2);
        String str2 = timeSlot.substring(6,8);
        int begin = Integer.parseInt(str1) - 9;
        int end = Integer.parseInt(str2) - 9;
        System.out.println(bookingField[dayOfWeek][begin]);
        for (int i = begin; i < end; i++) {
            if(bookingField[dayOfWeek][i])
                return true;
        }
        return false;
    }
    void cancel(int dayOfWeek, String timeSlot){
        String str1 = timeSlot.substring(0,2);
        String str2 = timeSlot.substring(6,8);
        int begin = Integer.parseInt(str1) - 9;
        int end = Integer.parseInt(str2) - 9;
        for (int i = begin; i < end; i++) {
            bookingField[dayOfWeek][i] = false;
        }
    }
    public void book(int dayOfWeek, String timeSlot){
        String str1 = timeSlot.substring(0,2);
        String str2 = timeSlot.substring(6,8);
        int begin = Integer.parseInt(str1) - 9;
        int end = Integer.parseInt(str2) - 9;
        for (int i = begin; i < end; i++) {
            bookingField[dayOfWeek][i] = true;
        }
    }
}