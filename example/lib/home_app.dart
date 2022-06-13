import 'package:flutter/material.dart';
import 'package:simple_bg_location_example/ui/home/home_page.dart';

class HomeApp extends StatelessWidget {
  const HomeApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      theme: ThemeData(
        colorScheme: ColorScheme.fromSwatch(
          primarySwatch: Colors.green,
        ).copyWith(secondary: Colors.amber),
        textTheme: const TextTheme(bodyText2: TextStyle(color: Colors.purple)),
        listTileTheme: const ListTileThemeData(
          dense: true,
          visualDensity: VisualDensity(horizontal: 0, vertical: -2.0),
          // shape: StadiumBorder(
          //     side: BorderSide(
          //         width: 2.0, color: Theme.of(context).primaryColor)),
        ),
      ),
      home: const HomePage(),
    );
  }
}
