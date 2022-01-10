// This is a basic Flutter widget test.
//
// To perform an interaction with a widget in your test, use the WidgetTester
// utility that Flutter provides. For example, you can send tap and scroll
// gestures. You can also use WidgetTester to find child widgets in the widget
// tree, read text, and verify that the values of widget properties are correct.

import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';

void main(List<String> args) {
  TestWidgetsFlutterBinding.ensureInitialized();
  MethodChannel? platform;

  setUpAll(() {
    platform = const MethodChannel('flutter/MethodChannelDemo');
  });
  test('test', () async {
    String plainText = 'saddam';
    String? enc = await platform!.invokeMethod('encript', {"text": plainText});
    String? dec = await platform!.invokeMethod('decript', {"text": enc});
    expect(plainText, dec);
  });
}

// void main() {
//   MethodChannel platform = const MethodChannel('flutter/MethodChannelDemo');

//   test('Test method channel function', () async {
//     String plainText = 'saddam';
//     await platform.invokeMockMethod('encript', {"text": plainText});
//     String? enc = await platform.invokeMethod('encript', {"text": plainText});
//     String? dec = await platform.invokeMethod('decript', {"text": enc});
//     print(dec);
//     expect(plainText, dec);
//   });
// }

// extension MethodChannelMock on MethodChannel {
//   Future<String?> invokeMockMethod(String method, dynamic arguments) async {
//     const codec = StandardMethodCodec();
//     final data = codec.encodeMethodCall(MethodCall(method, arguments));
//     ByteData? result;
//     ServicesBinding.instance?.defaultBinaryMessenger
//         .handlePlatformMessage(name, data, (ByteData? data) {
//       result = data;
//     });

//     return codec.decodeEnvelope(data) as String;
//   }
// }

// Future<T?> invokeMockMethod<T>(String method,
//     {required bool missingOk, dynamic arguments}) async {
//   const codec = StandardMethodCodec();

//   final ByteData? result =
//       await ServicesBinding.instance?.defaultBinaryMessenger.send(
//     method,
//     codec.encodeMethodCall(MethodCall(method, arguments)),
//   );
//   if (result == null) {
//     if (missingOk) {
//       return null;
//     }
//     throw MissingPluginException(
//         'No implementation found for method $method on channel $name');
//   }
//   return codec.decodeEnvelope(result) as T?;
// }
