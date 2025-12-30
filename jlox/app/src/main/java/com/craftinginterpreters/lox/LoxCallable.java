package com.craftinginterpreters.lox;

import java.util.List;

public interface LoxCallable {
  /**
   * Says the arity of the function, essentially how many arguments it has.
   * 
   * @return int
   */
  int arity();

  /**
   * Actual call
   * 
   * @param interpreter
   * @param arguments
   * @return
   */
  Object call(Interpreter interpreter, List<Object> arguments);
}
