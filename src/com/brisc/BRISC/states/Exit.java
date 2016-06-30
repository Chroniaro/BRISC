/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brisc.BRISC.states;

import com.brisc.BRISC.BaconRidingIntelligentSpaceCats;
import java.awt.Graphics2D;

/**
 *
 * @author Zac
 */
public class Exit extends GamePhase {

    @Override
    public void update() {
    }

    @Override
    public void render(Graphics2D g2d) {
    }

    @Override
    public void init() {
        BaconRidingIntelligentSpaceCats.close();
    }

    @Override
    public String toString() {
        return "Exit";
    }
    
}
