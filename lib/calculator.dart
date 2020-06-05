class Calculator {
  int sum(List<int> values) =>
      values.reduce((value, element) => value + element);

  int div(List<int> values) => values.reduce((value, element) => value ~/ element);
}
