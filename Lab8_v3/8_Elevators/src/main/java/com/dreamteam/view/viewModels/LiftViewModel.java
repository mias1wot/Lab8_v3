package com.dreamteam.view.viewModels;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LiftViewModel {
    private int liftNumber;
    private int floor;
    private int curPassengersCount;
}
