import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import 'package:simple_bg_location_example/home_app.dart';
import 'package:simple_bg_location_example/other_apis_app.dart';

import 'cubit/position_cubit.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return BlocProvider(
      create: (context) => PositionCubit(),
      child: const HomeApp(),
    );
  }
}
