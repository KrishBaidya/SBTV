import { createBrowserRouter } from "react-router";
import { MainLayout } from "./layouts/MainLayout";
import { SplashScreen } from "./pages/SplashScreen";
import { AddPlaylist } from "./pages/AddPlaylist";
import { Home } from "./pages/Home";
import { TVSection } from "./pages/TVSection";
import { Movies } from "./pages/Movies";
import { Series } from "./pages/Series";
import { Settings } from "./pages/Settings";
import { Player } from "./pages/Player";

export const router = createBrowserRouter([
  {
    path: "/",
    Component: SplashScreen,
  },
  {
    path: "/add-playlist",
    Component: AddPlaylist,
  },
  {
    path: "/",
    Component: MainLayout,
    children: [
      { path: "home", Component: Home },
      { path: "tv", Component: TVSection },
      { path: "movies", Component: Movies },
      { path: "series", Component: Series },
      { path: "settings", Component: Settings },
    ],
  },
  {
    path: "/player/:id",
    Component: Player,
  },
]);
