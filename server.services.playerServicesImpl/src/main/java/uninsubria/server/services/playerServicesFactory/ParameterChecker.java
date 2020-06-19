package uninsubria.server.services.playerServicesFactory;

import java.lang.reflect.Constructor;

import uninsubria.server.services.playerServicesTypes.PlayerServiceType;

class ParameterChecker {
	
	public boolean check(PlayerServiceType type, Object[] givenPar) {
		if (type == null | givenPar == null) {
			return false;
		}
		Constructor<?>[] constr = type.getValue().getConstructors();
		for (Constructor<?> c : constr) {
			Class<?>[] cParam = c.getParameterTypes();
			if (cParam.length != givenPar.length) {
				continue;
			} else if (cParam.length == 0 & givenPar.length == 0) {
				return true;
			} else {
				int i = 0;
				boolean allMatch = false;
				for (Class<?> pType : cParam) {
					if (givenPar[i] == null || pType.isInstance(givenPar[i]) || pType.isAssignableFrom(givenPar[i].getClass())) {
						allMatch = true;
						i++;
					} else {
						allMatch = false;
						break;
					}
				}
				if (allMatch) {
					return true;
				}
			}
		}
		return false;
		
	}
}
