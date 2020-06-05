import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:platform_channel_sandbox/calculator.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  MethodChannel('samples.flutter.dev/dart')
      .setMethodCallHandler(calcHandler);
  runApp(MyApp());
}

Future<dynamic> calcHandler(MethodCall methodCall) async {
  switch (methodCall.method) {
    case 'sum':
      print(methodCall.arguments);
      return Calculator().sum(methodCall.arguments.cast<int>());
    case 'div':
      return Calculator().div(methodCall.arguments.cast<int>());
    default:
      throw UnimplementedError();
  }
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Platform Channel Sandbox',
      theme: ThemeData(
        primarySwatch: Colors.blue,
        visualDensity: VisualDensity.adaptivePlatformDensity,
      ),
      home: MyHomePage(title: 'Platform Channel Sandbox'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  MyHomePage({Key key, this.title}) : super(key: key);

  final String title;

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  static const platform = const MethodChannel('samples.flutter.dev/battery');

  String _batteryLevel = 'Unknown battery level.';

  Future<void> _getBatteryLevel() async {
    String batteryLevel;
    try {
      final int result = await platform.invokeMethod('getBatteryLevel');
      batteryLevel = 'Battery level at $result % .';
    } on PlatformException catch (e) {
      batteryLevel = "Failed to get battery level: '${e.message}'.";
    }

    setState(() {
      _batteryLevel = batteryLevel;
    });
  }

  Widget build(BuildContext context) {
    return Material(
      child: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
          children: [
            RaisedButton(
              child: Text('Get Battery Level'),
              onPressed: _getBatteryLevel,
            ),
            Text(_batteryLevel),
          ],
        ),
      ),
    );
  }
}
