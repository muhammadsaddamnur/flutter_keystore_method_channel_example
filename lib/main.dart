import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({Key? key, required this.title}) : super(key: key);
  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  static const platform = MethodChannel('flutter/MethodChannelDemo');
  String encriptText = '';
  String decriptText = '';
  String publicKeyText = '';
  List keyAlias = [];

  Future<void> encrypt() async {
    try {
      final String enc =
          await platform.invokeMethod('encript', {"text": "saddam"});
      final String dec = await platform.invokeMethod('decript', {"text": enc});
      final String getPublicKey =
          await platform.invokeMethod('getPublicKey', {"base64Encode": true});

      setState(() {
        encriptText = enc;
        decriptText = dec;
        publicKeyText = getPublicKey;
      });
    } on PlatformException catch (e) {
      setState(() {
        encriptText = e.toString();
        decriptText = e.toString();
      });
    }
  }

  @override
  void initState() {
    Future.microtask(() async {
      keyAlias = await platform.invokeMethod('listKeyAlias');
      setState(() {});
    });
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: ListView(
        children: <Widget>[
          const Text('Plain Text\nsaddam'),
          const Divider(
            thickness: 1,
          ),
          Text(
            '\nEncript Text:\n$encriptText',
          ),
          const Divider(
            thickness: 1,
          ),
          Text(
            '\nDecript Text:\n$decriptText',
          ),
          const Divider(
            thickness: 1,
          ),
          Text(
            '\nList KeyAlias:\n$keyAlias',
          ),
          const Divider(
            thickness: 1,
          ),
          Text(
            '\nPublicKey:\n$publicKeyText',
          ),
        ],
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: encrypt,
        tooltip: 'Increment',
        child: const Icon(Icons.add),
      ),
    );
  }
}
