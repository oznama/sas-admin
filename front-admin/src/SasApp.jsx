import { AuthProvider } from './app/components/auth/context/AuthProvider';
import { MainRouter } from './router/MainRouter';

export const SasApp = () => (
    <AuthProvider>
        <MainRouter />
    </AuthProvider>
);
