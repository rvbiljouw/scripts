package me.rvbiljouw.rsbot.pickpocket.model;

import java.util.Arrays;

/**
 * @author rvbiljouw
 */
public final class PickpocketSettings {
    private String npcName;
    private String foodName;
    private String[] dropAllExcept;
    private int eatAt;

    public String getNpcName() {
        return npcName;
    }

    public void setNpcName(String npcName) {
        this.npcName = npcName;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public int getEatAt() {
        return eatAt;
    }

    public void setEatAt(int eatAt) {
        this.eatAt = eatAt;
    }

    public String[] getDropAllExcept() {
        return dropAllExcept;
    }

    public void setDropAllExcept(String[] dropAllExcept) {
        this.dropAllExcept = dropAllExcept;
    }

    @Override
    public String toString() {
        return "PickpocketSettings{" +
                "npcName='" + npcName + '\'' +
                ", foodName='" + foodName + '\'' +
                ", dropAllExcept=" + Arrays.toString(dropAllExcept) +
                ", eatAt=" + eatAt +
                '}';
    }
}
