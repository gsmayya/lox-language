package com.craftinginterpreters.lox;

import java.util.List;

public class NativeFunctions {

  public static LoxCallable clockNative() {
    return new LoxCallable() {
      @Override
      public int arity() {
        return 0;
      }

      @Override
      public Object call(Interpreter interpreter, List<Object> arguments) {
        return (double) System.currentTimeMillis() / 1000.0;
      }

      @Override
      public String toString() {
        return "<native fn>";
      }
    };
  }

  public static LoxCallable clockMilliNative() {
    return new LoxCallable() {
      @Override
      public int arity() {
        return 0;
      }

      @Override
      public Object call(Interpreter interpreter, List<Object> arguments) {
        return (double) System.currentTimeMillis();
      }

      @Override
      public String toString() {
        return "<native fn>";
      }
    };
  }

}
