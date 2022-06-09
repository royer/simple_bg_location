import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:simple_bg_location/simple_bg_location.dart';
import 'package:simple_bg_location_example/cubit/position_cubit.dart';

class MyBottomBar extends StatelessWidget {
  MyBottomBar({
    Key? key,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return BottomAppBar(
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 5.0),
        child: Row(
          mainAxisSize: MainAxisSize.max,
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            IconButton(
              onPressed: () {},
              icon: const Icon(Icons.gps_fixed),
            ),
            const Text("distance:"),
            SizedBox(
              width: 54.0,
              child: BlocBuilder<PositionCubit, PositionState>(
                builder: (context, state) {
                  return ElevatedButton(
                    onPressed: () {
                      if (!state.isTracking)
                        _requestPositionUpdate(context);
                      else
                        _stopPositionUpdate(context);
                    },
                    child:
                        Icon((!state.isTracking) ? Icons.play_arrow : Icons.stop),
                  );
                },
              ),
            ),
          ],
        ),
      ),
    );
  }

  void _requestPositionUpdate(BuildContext context) {
    context.read<PositionCubit>().requestPositionUpdate(RequestSettings());
  }

  void _stopPositionUpdate(BuildContext context) {
    context.read<PositionCubit>().stopPositionUpdate();
  }
}
