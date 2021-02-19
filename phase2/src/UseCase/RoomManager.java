package UseCase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import Entities.Room;

/**
 * A Use Case class that manages the functionality of room.
 */
public class RoomManager implements Serializable {
    private final List<Room> allRoom;

    /**
     * A constructor for a RoomManager that initializes the list of all room as an empty list.
     */
    public RoomManager() {
        allRoom = new ArrayList<>();
    }

    /**
     * Create a new room.
     * @param roomNum the room number of the new room.
     */
    public void createRoom(String roomNum, int capacity){ allRoom.add(new Room(roomNum, capacity));
    }

    /**
     * Check whether the room exists.
     * @param roomNum the room number of the room that you want to check.
     * @return true iff the room has existed. Otherwise, return false.
     */
    public boolean doesRoomExist(String roomNum){
        for(Room room: allRoom){
            if(room.getRoomNum().equals(roomNum)) return true;
        }
        return false;
    }

    /**
     * Precondition: the room exits.
     * Get the room object by the room number.
     * @param roomNum the room number of the room that you want to find.
     * @return the room object with given room number.
     */
    private Room stringToRoom(String roomNum){
        assert doesRoomExist(roomNum);

        for (Room room: allRoom){
            if (room.getRoomNum().equals(roomNum)) { return room; }
        }
        throw new IllegalArgumentException("There is no such a speaker with the username. ");
    }

    /**
     * Get the room capacity by room number
     * @param roomNum the room number of the room
     * @return if the room exits, return the capacity of the room. Otherwise, return 0.
     */
    public int getCapacity(String roomNum){
        if (doesRoomExist(roomNum)){
            return stringToRoom(roomNum).getCapacity();
        }
        return 0;
    }
}
