import { AuthProvider } from './app/components/auth/context/AuthProvider';
import { LoadingProvider } from './app/components/custom/loading/context/LoadingProvider';
import { MainRouter } from './router/MainRouter';

export const SasApp = () => (
    <AuthProvider>
        <LoadingProvider>
            <MainRouter />
        </LoadingProvider>
    </AuthProvider>
);
