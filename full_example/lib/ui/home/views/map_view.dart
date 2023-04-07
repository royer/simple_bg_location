import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:flutter_map/flutter_map.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:latlong2/latlong.dart';
import 'package:simple_bg_location/simple_bg_location.dart';
import '../../../cubit/location/location_cubit.dart';

class MapView extends StatefulWidget {
  const MapView({Key? key}) : super(key: key);

  @override
  State<MapView> createState() => _MapViewState();
}

class _MapViewState extends State<MapView> with AutomaticKeepAliveClientMixin {
  late MapController _mapController;

  final _center = LatLng(49.284260, -123.132448);

  late MapOptions _mapOptions;

  final List<LatLng> _trace = [];

  final List<CircleMarker> _positions = [];

  final List<CircleMarker> _currentPositionResult = [];

  @override
  void initState() {
    super.initState();
    _mapController = MapController();

    _mapOptions = MapOptions(
      center: _center,
      zoom: 13.0,
      onPositionChanged: _onPositionChanged,
    );
  }

  @override
  void dispose() {
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    super.build(context);
    return BlocListener<LocationCubit, LocationState>(
      listener: (context, state) {
        if (state is PositionArrived) {
          final position = state.newPosition;
          _onNewPositionArrived(position);
        } else if (state is PositionUpdateState) {
          if (state.isTracking) {
            if (state.positions.isNotEmpty) {
              for (final position in state.positions) {
                _onNewPositionArrived(position);
              }
            }
          } else {
            _clear();
            setState(() {});
          }
        } else if (state is PositionCurrentPositionResult) {
          final ll = LatLng(
              state.currentResult.latitude, state.currentResult.longitude);
          _updateCurrentPositionResultMarker(ll);
        } else if (state is PositionStateError) {
          final errorState = state;
          Fluttertoast.showToast(
              msg: '${errorState.error}', toastLength: Toast.LENGTH_LONG);
          _clear();
          setState(() {});
        }
      },
      child: FlutterMap(
        mapController: _mapController,
        options: _mapOptions,
        children: [
          TileLayer(
            urlTemplate: "https://tile.openstreetmap.org/{z}/{x}/{y}.png",
            userAgentPackageName: "com.royzed.simple_bg_location_example",
          ),
          PolylineLayer(polylineCulling: false, polylines: [
            Polyline(
              points: _trace,
              strokeWidth: 10.0,
              color: const Color.fromRGBO(0, 179, 253, 0.8),
            ),
          ]),
          CircleLayer(
            circles: _positions,
          ),
          CircleLayer(
            circles: _currentPositionResult,
          ),
        ],
      ),
    );
  }

  void _onPositionChanged(MapPosition pos, bool hasGesture) {
    _mapOptions.crs.scale(_mapController.zoom);
  }

  void _onNewPositionArrived(Position position) {
    LatLng ll = LatLng(position.latitude, position.longitude);
    _mapController.move(ll, _mapController.zoom);

    _trace.add(ll);

    //_positions.add(CircleMarker(point: ll, radius: 5.0, color: Colors.black));
    _positions.add(CircleMarker(
        point: ll,
        radius: 3.0,
        color: Colors.amber,
        borderColor: Colors.black,
        borderStrokeWidth: 1));
  }

  void _updateCurrentPositionResultMarker(LatLng ll) {
    _currentPositionResult.clear();
    _mapController.move(ll, 13);
    _currentPositionResult.add(
        CircleMarker(point: ll, radius: 15, color: const Color(0x80FF0000)));
  }

  void _clear() {
    _trace.clear();
    _positions.clear();
  }

  @override
  bool get wantKeepAlive => true;
}
