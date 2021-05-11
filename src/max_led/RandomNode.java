/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package max_led;

/**
 *
 * @author Mohanad
 */

//Used for random generation
public class RandomNode implements Comparable<RandomNode>{
    int index;
    double random;
    
    public RandomNode(int index, double random){
        this.index = index;
        this.random = random;
    }

    @Override
    public int compareTo(RandomNode o) {
        if(this.random > o.random){
            return 1;
        }
        return -1;
    }
    
    
}
