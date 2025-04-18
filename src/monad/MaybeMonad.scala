import shared.*

// --- 4. Maybe Monad の使用例 ---
@main def runMaybeMonad(): Unit = {
  println("--- Maybe Monad Usage Example ---")

  // Monad インスタンスの取得
  val M = MaybeMonad

  // Maybe の値を作成
  val maybeFive: Maybe[Int] = Just(5)
  val maybeTen: Maybe[Int] = M.pure(10) // pure を使用
  val noValue: Maybe[Int] = Nothing

  println(s"maybeFive: $maybeFive")
  println(s"maybeTen (via pure): $maybeTen")
  println(s"noValue: $noValue")

  // map の使用例
  val maybeSix = M.map(maybeFive)(_ + 1) // Just(5) -> Just(6)
  val mapNothing = M.map(noValue)(_ + 1)  // Nothing -> Nothing
  println(s"M.map(maybeFive)(_ + 1): $maybeSix")
  println(s"M.map(noValue)(_ + 1): $mapNothing")

  // flatMap の使用例 (安全な除算)
  def safeDivide(numerator: Int, denominator: Int): Maybe[Double] = {
    if (denominator == 0) Nothing
    else Just(numerator.toDouble / denominator)
  }

  // Just(10) を Just(5) で割る
  val divideTenByFive: Maybe[Double] = M.flatMap(maybeTen) { ten => // ten = 10
    M.flatMap(maybeFive) { five => // five = 5
      safeDivide(ten, five)       // safeDivide(10, 5) -> Just(2.0)
    }
  }
  println(s"Divide 10 by 5 using flatMap: $divideTenByFive") // Just(2.0)

  // Just(10) を 0 (Nothing 経由) で割る
  val divideTenByNothing: Maybe[Double] = M.flatMap(maybeTen) { ten =>
    M.flatMap(noValue) { zero => // noValue は Nothing なので、この flatMap は Nothing を返す
      safeDivide(ten, zero)      // この部分は実行されない
    }
  }
  println(s"Divide 10 by Nothing using flatMap: $divideTenByNothing") // Nothing

  // Just(10) を 0 で割る (safeDivide が Nothing を返すケース)
  val divideTenByZero: Maybe[Double] = M.flatMap(maybeTen) { ten =>
    safeDivide(ten, 0) // safeDivide(10, 0) -> Nothing
  }
  println(s"Divide 10 by 0 using flatMap: $divideTenByZero") // Nothing


  println("\n--- About for-comprehension ---")
  println("To use for-comprehensions like `for { x <- maybeX ... }`,")
  println("the `Maybe` type itself needs `flatMap`, `map`, and `withFilter` methods,")
  println("either directly defined or added via implicit conversions.")
  println("Our current `Maybe` type doesn't have them directly.")
  println("You could define an implicit class like this:")

  println("\n--- Using for-comprehension with implicit class ---")

  val maybeX: Maybe[Int] = Just(10)
  val maybeY: Maybe[Int] = Just(5)
  val maybeZ: Maybe[Int] = Nothing

  val sumXY: Maybe[Int] = for {
    x <- maybeX
    y <- maybeY
    if x > 0 // フィルター条件
  } yield x + y
  println(s"for { x <- Just(10); y <- Just(5); if x > 0 } yield x + y: $sumXY") // Just(15)

  val sumXZ: Maybe[Int] = for {
    x <- maybeX
    z <- maybeZ // ここで Nothing が入る
    y <- maybeY // 実行されない
  } yield x + y + z // 実行されない
  println(s"for { x <- Just(10); z <- Nothing; ... } yield ...: $sumXZ") // Nothing

}