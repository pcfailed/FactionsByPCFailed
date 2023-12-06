// 
// Decompiled by Procyon v0.5.36
// 

package org.pcfailed.factions.extras;

public class Pair<A, B>
{
    protected A a;
    protected B b;
    
    public Pair(final A a, final B b) {
        this.a = a;
        this.b = b;
    }
    
    public A getA() {
        return this.a;
    }
    
    public B getB() {
        return this.b;
    }
}
